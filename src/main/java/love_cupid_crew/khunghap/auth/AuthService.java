package love_cupid_crew.khunghap.auth;

import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.auth.dto.LoginRequest;
import love_cupid_crew.khunghap.auth.dto.LoginResponse;
import love_cupid_crew.khunghap.auth.dto.RegisterRequest;
import love_cupid_crew.khunghap.auth.dto.TokenResponse;
import love_cupid_crew.khunghap.global.jwt.JwtProvider;
import love_cupid_crew.khunghap.global.security.CustomUserDetails;
import love_cupid_crew.khunghap.ilju.entity.IljuAnimal;
import love_cupid_crew.khunghap.user.entity.User;
import love_cupid_crew.khunghap.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private static final String REDIS_PREFIX = "refresh:token:";

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        CustomUserDetails userDetails = CustomUserDetails.from(user);
        String accessToken = jwtProvider.generateAccessToken(userDetails);
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        redisTemplate.opsForValue()
                .set(REDIS_PREFIX + user.getId(), refreshToken, refreshExpiration, TimeUnit.MILLISECONDS);

        IljuAnimal animal = user.getIljuAnimal();
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .nickname(user.getNickname())
                .iljuAnimal(animal != null ? animal.getName() : null)
                .iljuOheng(animal != null ? animal.getOheng() : null)
                .build();
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken) || jwtProvider.isAccessToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 refresh 토큰입니다.");
        }

        Long userId = jwtProvider.getUserId(refreshToken);

        String stored = redisTemplate.opsForValue().get(REDIS_PREFIX + userId);
        if (!refreshToken.equals(stored)) {
            throw new IllegalArgumentException("유효하지 않은 refresh 토큰입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        CustomUserDetails userDetails = CustomUserDetails.from(user);
        String newAccessToken = jwtProvider.generateAccessToken(userDetails);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        redisTemplate.opsForValue()
                .set(REDIS_PREFIX + userId, newRefreshToken, refreshExpiration, TimeUnit.MILLISECONDS);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        redisTemplate.delete(REDIS_PREFIX + userId);
    }
}