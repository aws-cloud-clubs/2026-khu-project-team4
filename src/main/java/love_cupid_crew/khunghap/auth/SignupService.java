package love_cupid_crew.khunghap.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import love_cupid_crew.khunghap.auth.dto.*;
import love_cupid_crew.khunghap.global.jwt.JwtProvider;
import love_cupid_crew.khunghap.global.s3.S3Service;
import love_cupid_crew.khunghap.global.security.CustomUserDetails;
import love_cupid_crew.khunghap.ilju.IljuCalculator;
import love_cupid_crew.khunghap.ilju.entity.IljuAnimal;
import love_cupid_crew.khunghap.user.entity.*;
import love_cupid_crew.khunghap.user.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignupService {

    private final UserRepository userRepository;
    private final UserLifestyleRepository userLifestyleRepository;
    private final UserHobbyRepository userHobbyRepository;
    private final UserDatingStyleRepository userDatingStyleRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final UserPhotoRepository userPhotoRepository;
    private final CoinBalanceRepository coinBalanceRepository;

    private final IljuCalculator iljuCalculator;
    private final S3Service s3Service;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private static final String REFRESH_PREFIX  = "refresh:token:";
    private static final String VERIFIED_PREFIX = "email:verified:";
    private static final int    INITIAL_COINS   = 3;

    public PresignedResponse generatePresignedUrls(int count) {
        List<PresignedResponse.PhotoUrl> photos = java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> {
                    S3Service.PresignedPutResult result = s3Service.generatePresignedPutUrl();
                    return PresignedResponse.PhotoUrl.builder()
                            .presignedUrl(result.presignedUrl())
                            .tempKey(result.tempKey())
                            .expiresIn(300)
                            .build();
                })
                .toList();

        return PresignedResponse.builder().photos(photos).build();
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        validateVerifiedToken(request.getVerifiedToken());
        validateTerms(request.getTerms());

        String email = jwtProvider.getEmailFromToken(request.getVerifiedToken());
        validateDuplicates(email, request.getNickname());
        validatePhotosExistOnS3(request.getPhotos());

        IljuAnimal iljuAnimal = iljuCalculator.calculate(request.getBirthDate());

        User user = userRepository.save(User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .birthHour(request.getBirthHour())
                .gender(request.getGender())
                .college(request.getCollege())
                .iljuAnimal(iljuAnimal)
                .build());

        saveLifestyle(user, request.getLifestyle());
        saveHobbies(user, request.getHobbies());
        saveDatingStyle(user, request.getDatingStyle());
        savePreferences(user, request.getPreferences());
        savePhotos(user, request.getPhotos());
        saveCoinBalance(user);

        consumeVerifiedToken(request.getVerifiedToken());
        eventPublisher.publishEvent(new love_cupid_crew.khunghap.match.UserSignedUpEvent(user.getId()));
        log.info("회원가입 완료: userId={}, email={}, iljuAnimal={}", user.getId(), email, iljuAnimal.getName());

        String accessToken  = jwtProvider.generateAccessToken(CustomUserDetails.from(user));
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());
        redisTemplate.opsForValue()
                .set(REFRESH_PREFIX + user.getId(), refreshToken, refreshExpiration, TimeUnit.MILLISECONDS);

        return SignupResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(SignupResponse.UserInfo.builder()
                        .id(user.getId())
                        .nickname(user.getNickname())
                        .iljuAnimal(iljuAnimal.getName())
                        .iljuOheng(iljuAnimal.getOheng())
                        .iljuEmoji(iljuAnimal.getEmoji())
                        .coins(INITIAL_COINS)
                        .build())
                .build();
    }

    private void validateVerifiedToken(String token) {
        if (!jwtProvider.validateToken(token) || !jwtProvider.isVerifiedToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 인증 토큰입니다.");
        }
        String email = jwtProvider.getEmailFromToken(token);
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(VERIFIED_PREFIX + email))) {
            throw new IllegalArgumentException("이메일 인증이 만료되었습니다. 다시 인증해 주세요.");
        }
    }

    private void consumeVerifiedToken(String token) {
        String email = jwtProvider.getEmailFromToken(token);
        redisTemplate.delete(VERIFIED_PREFIX + email);
    }

    private void validateTerms(SignupRequest.TermsRequest terms) {
        if (!terms.isAgeVerified() || !terms.isServiceAgreed() || !terms.isPrivacyAgreed()) {
            throw new IllegalArgumentException("필수 약관에 동의해야 합니다.");
        }
    }

    private void validateDuplicates(String email, String nickname) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
    }

    private void validatePhotosExistOnS3(List<SignupRequest.PhotoRequest> photos) {
        for (SignupRequest.PhotoRequest photo : photos) {
            if (!s3Service.doesObjectExist(photo.getTempKey())) {
                throw new IllegalArgumentException("업로드되지 않은 사진이 있습니다: " + photo.getTempKey());
            }
        }
    }

    private void saveLifestyle(User user, SignupRequest.LifestyleRequest req) {
        userLifestyleRepository.save(UserLifestyle.builder()
                .user(user)
                .emojiIntro(req.getEmojiIntro())
                .mbti(req.getMbti())
                .drinking(req.getDrinking())
                .smoking(req.getSmoking())
                .exercise(req.getExercise())
                .build());
    }

    private void saveHobbies(User user, List<String> hobbies) {
        List<UserHobby> entities = hobbies.stream()
                .map(hobby -> UserHobby.builder().user(user).hobby(hobby).build())
                .toList();
        userHobbyRepository.saveAll(entities);
    }

    private void saveDatingStyle(User user, SignupRequest.DatingStyleRequest req) {
        userDatingStyleRepository.save(UserDatingStyle.builder()
                .user(user)
                .purpose(req.getPurpose())
                .contactStyle(req.getContactStyle())
                .dateStyle(req.getDateStyle())
                .personalTime(req.getPersonalTime())
                .ageMin(req.getAgeMin())
                .ageMax(req.getAgeMax())
                .heightMin(req.getHeightMin())
                .heightMax(req.getHeightMax())
                .build());
    }

    private void savePreferences(User user, List<SignupRequest.PreferenceRequest> preferences) {
        List<UserPreference> entities = preferences.stream()
                .map(p -> UserPreference.builder()
                        .user(user)
                        .category(p.getCategory())
                        .score(p.getScore())
                        .build())
                .toList();
        userPreferenceRepository.saveAll(entities);
    }

    private void savePhotos(User user, List<SignupRequest.PhotoRequest> photos) {
        for (SignupRequest.PhotoRequest photo : photos) {
            String profileKey = s3Service.copyToProfiles(photo.getTempKey());
            userPhotoRepository.save(UserPhoto.builder()
                    .user(user)
                    .imageUrl(profileKey)
                    .displayOrder(photo.getDisplayOrder())
                    .build());
        }
    }

    private void saveCoinBalance(User user) {
        coinBalanceRepository.save(CoinBalance.builder()
                .user(user)
                .balance(INITIAL_COINS)
                .build());
    }
}