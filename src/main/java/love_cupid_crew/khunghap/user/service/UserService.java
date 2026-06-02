package love_cupid_crew.khunghap.user.service;

import love_cupid_crew.khunghap.user.dto.*;
import love_cupid_crew.khunghap.user.entity.*;
import love_cupid_crew.khunghap.user.repository.UserRepository;
import love_cupid_crew.khunghap.user.enums.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DummyPhotoService dummyPhotoService;

    /**
     * 사용자 ID로 전체 프로필 정보를 조회하고 DTO로 변환하여 반환
     * N+1 문제가 해결된 최적화된 쿼리 사용
     */
    public UserProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findByIdWithDetails(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return convertToUserProfileResponse(user);
    }

    /**
     * User 엔티티를 UserProfileResponse DTO로 변환
     * Builder 패턴을 사용한 수동 매핑
     */
    private UserProfileResponse convertToUserProfileResponse(User user) {
        // 사진 목록 변환 (display_order 기준 정렬)
        List<PhotoDto> photos = user.getUserPhotos() != null
                ? user.getUserPhotos().stream()
                    .sorted((p1, p2) -> p1.getDisplayOrder().compareTo(p2.getDisplayOrder()))
                    .map(photo -> PhotoDto.builder()
                            .id(photo.getId())
                            .displayOrder(photo.getDisplayOrder())
                            .imageUrl(photo.getImageUrl())
                            .build())
                    .collect(Collectors.toList())
                : List.of();

        // 라이프스타일 변환
        LifestyleDto lifestyle = user.getUserLifestyle() != null
                ? LifestyleDto.builder()
                    .emojiIntro(user.getUserLifestyle().getEmojiIntro())
                    .mbti(user.getUserLifestyle().getMbti())
                    .drinking(user.getUserLifestyle().getDrinking())
                    .smoking(user.getUserLifestyle().getSmoking())
                    .build()
                : null;

        // 취미 목록 변환
        List<String> hobbies = user.getUserHobbies() != null
                ? user.getUserHobbies().stream()
                    .map(UserHobby::getHobby)
                    .collect(Collectors.toList())
                : List.of();

        // 데이팅 스타일 변환
        DatingStyleDto datingStyle = user.getUserDatingStyle() != null
                ? DatingStyleDto.builder()
                    .purpose(user.getUserDatingStyle().getPurpose())
                    .contactStyle(user.getUserDatingStyle().getContactStyle())
                    .dateStyle(user.getUserDatingStyle().getDateStyle())
                    .personalTime(user.getUserDatingStyle().getPersonalTime())
                    .ageMin(user.getUserDatingStyle().getAgeMin())
                    .ageMax(user.getUserDatingStyle().getAgeMax())
                    .heightMin(user.getUserDatingStyle().getHeightMin())
                    .heightMax(user.getUserDatingStyle().getHeightMax())
                    .sameCollegeExcluded(user.getUserDatingStyle().isSameCollegeExcluded())
                    .build()
                : null;

        // 선호도 목록 변환
        List<PreferenceDto> preferences = user.getUserPreferences() != null
                ? user.getUserPreferences().stream()
                    .map(preference -> PreferenceDto.builder()
                            .category(preference.getCategory())
                            .score(preference.getScore())
                            .build())
                    .collect(Collectors.toList())
                : List.of();

        // 코인 잔액 조회
        int balance = user.getCoinBalance() != null
                ? user.getCoinBalance().getBalance()
                : 0;

        // 일주동물 정보
        String iljuAnimal = user.getIljuAnimal() != null
                ? user.getIljuAnimal().getName()
                : null;

        String iljuOheng = user.getIljuAnimal() != null
                ? user.getIljuAnimal().getOheng()
                : null;

        // 생년월일 포맷 변환 (LocalDate → String)
        String birthDate = user.getBirthDate() != null
                ? user.getBirthDate().toString()
                : null;

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .birthDate(birthDate)
                .gender(user.getGender())
                .college(user.getCollege())
                .iljuAnimal(iljuAnimal)
                .iljuOheng(iljuOheng)
                .isActive(user.isActive())
                .balance(balance)
                .photos(photos)
                .lifestyle(lifestyle)
                .hobbies(hobbies)
                .datingStyle(datingStyle)
                .preferences(preferences)
                .build();
    }

    @Transactional
    public UserProfileUpdateResponse updateProfile(Long userId, love_cupid_crew.khunghap.user.dto.UserProfileUpdateRequest req) {
        User user = userRepository.findByIdWithDetails(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // nickname
        if (req.getNickname() != null) {
            user.setNickname(req.getNickname());
        }

        // lifestyle
        UserProfileUpdateRequest.LifestyleRequest lreq = req.getLifestyle();
        if (lreq != null) {
            DrinkingHabit drinking = null;
            SmokingHabit smoking = null;
            try {
                if (lreq.getDrinking() != null) drinking = DrinkingHabit.valueOf(lreq.getDrinking());
            } catch (Exception ignored) {}
            try {
                if (lreq.getSmoking() != null) smoking = SmokingHabit.valueOf(lreq.getSmoking());
            } catch (Exception ignored) {}

            if (user.getUserLifestyle() != null) {
                user.getUserLifestyle().update(lreq.getEmojiIntro(), lreq.getMbti(), drinking, smoking);
            } else {
                UserLifestyle lifestyle = UserLifestyle.builder()
                        .emojiIntro(lreq.getEmojiIntro())
                        .mbti(lreq.getMbti())
                        .drinking(drinking == null ? love_cupid_crew.khunghap.user.enums.DrinkingHabit.NEVER : drinking)
                        .smoking(smoking == null ? love_cupid_crew.khunghap.user.enums.SmokingHabit.NON_SMOKER : smoking)
                        .build();
                user.setUserLifestyle(lifestyle);
            }
        }

        // hobbies (replace)
        if (req.getHobbies() != null) {
            java.util.List<UserHobby> newHobbies = new java.util.ArrayList<>();
            for (String h : req.getHobbies()) {
                UserHobby uh = UserHobby.builder().hobby(h).build();
                newHobbies.add(uh);
            }
            user.replaceHobbies(newHobbies);
        }

        // dating style
        UserProfileUpdateRequest.DatingStyleRequest dreq = req.getDatingStyle();
        if (dreq != null) {
            DatingPurpose purpose = null;
            ContactStyle contactStyle = null;
            DateStyle dateStyle = null;
            PersonalTime personalTime = null;
            try { if (dreq.getPurpose() != null) purpose = DatingPurpose.valueOf(dreq.getPurpose()); } catch (Exception ignored) {}
            try { if (dreq.getContactStyle() != null) contactStyle = ContactStyle.valueOf(dreq.getContactStyle()); } catch (Exception ignored) {}
            try { if (dreq.getDateStyle() != null) dateStyle = DateStyle.valueOf(dreq.getDateStyle()); } catch (Exception ignored) {}
            try { if (dreq.getPersonalTime() != null) personalTime = PersonalTime.valueOf(dreq.getPersonalTime()); } catch (Exception ignored) {}

            if (user.getUserDatingStyle() != null) {
                user.getUserDatingStyle().update(purpose, contactStyle, dateStyle, personalTime,
                        dreq.getAgeMin(), dreq.getAgeMax(), dreq.getHeightMin(), dreq.getHeightMax(), dreq.isSameCollegeExcluded());
            } else {
                UserDatingStyle uds = UserDatingStyle.builder()
                        .purpose(purpose == null ? love_cupid_crew.khunghap.user.enums.DatingPurpose.UNSURE : purpose)
                        .contactStyle(contactStyle == null ? love_cupid_crew.khunghap.user.enums.ContactStyle.NORMAL : contactStyle)
                        .dateStyle(dateStyle == null ? love_cupid_crew.khunghap.user.enums.DateStyle.MIXED : dateStyle)
                        .personalTime(personalTime == null ? love_cupid_crew.khunghap.user.enums.PersonalTime.MIXED : personalTime)
                        .ageMin(dreq.getAgeMin())
                        .ageMax(dreq.getAgeMax())
                        .heightMin(dreq.getHeightMin())
                        .heightMax(dreq.getHeightMax())
                        .sameCollegeExcluded(dreq.isSameCollegeExcluded())
                        .build();
                user.setUserDatingStyle(uds);
            }
        }

        // preferences (replace)
        if (req.getPreferences() != null) {
            java.util.List<UserPreference> newPrefs = new java.util.ArrayList<>();
            for (PreferenceDto p : req.getPreferences()) {
                UserPreference up = UserPreference.builder()
                        .category(p.getCategory())
                        .score(p.getScore())
                        .build();
                newPrefs.add(up);
            }
            user.replacePreferences(newPrefs);
        }

        // JPA 더티체킹으로 변경 사항이 자동 반영
        return UserProfileUpdateResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .build();
    }

    /**
     * 사용자의 매칭 활성화 상태를 변경
     * @Transactional을 통해 JPA 더티체킹으로 변경 사항 자동 반영
     */
    @Transactional
    public UserActiveUpdateResponse updateActiveStatus(Long userId, UserActiveUpdateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        user.setIsActive(req.getIsActive());

        return UserActiveUpdateResponse.builder()
                .isActive(user.isActive())
                .build();
    }

    /**
     * 사용자의 프로필 사진을 수정
     * 1. deleteIds에 해당하는 기존 사진들 삭제
     * 2. 새 파일 업로드 및 UserPhoto 엔티티 생성
     * 3. displayOrder 정보 반영
     * @Transactional을 통해 모든 변경사항 자동 적용
     */
    @Transactional
    public UserPhotoUpdateResponse updatePhotos(Long userId, MultipartFile[] photos, List<Long> deleteIds, List<Short> displayOrders) {
        User user = userRepository.findByIdWithDetails(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 1. deleteIds에 해당하는 사진 삭제
        if (deleteIds != null && !deleteIds.isEmpty()) {
            if (user.getUserPhotos() != null) {
                user.getUserPhotos().removeIf(photo -> deleteIds.contains(photo.getId()));
            }
        }

        // 2. 새 파일 업로드
        java.util.List<UserPhoto> newPhotos = new java.util.ArrayList<>();
        if (photos != null && photos.length > 0) {
            List<String> uploadedUrls = dummyPhotoService.uploadPhotos(photos);

            // displayOrder가 없으면 1부터 시작
            int order = 1;
            if (displayOrders == null || displayOrders.isEmpty()) {
                // 기존 사진의 최대 order + 1부터 시작
                if (user.getUserPhotos() != null && !user.getUserPhotos().isEmpty()) {
                    order = (int) user.getUserPhotos().stream()
                            .map(UserPhoto::getDisplayOrder)
                            .max(Short::compare)
                            .orElse((short) 0) + 1;
                }
            }

            // 3. 새 UserPhoto 엔티티 생성
            for (int i = 0; i < uploadedUrls.size(); i++) {
                Short displayOrder = (displayOrders != null && i < displayOrders.size())
                        ? displayOrders.get(i)
                        : (short) (order + i);

                UserPhoto userPhoto = UserPhoto.builder()
                        .imageUrl(uploadedUrls.get(i))
                        .displayOrder(displayOrder)
                        .build();
                newPhotos.add(userPhoto);
            }

            // 4. 기존 사진이 없으면 새 사진들로 초기화, 있으면 추가
            if (user.getUserPhotos() == null || user.getUserPhotos().isEmpty()) {
                user.replacePhotos(newPhotos);
            } else {
                // 기존 리스트에 새 사진들 추가
                for (UserPhoto photo : newPhotos) {
                    photo.setUser(user);
                    user.getUserPhotos().add(photo);
                }
            }
        }

        // 5. 응답 DTO로 변환
        List<PhotoDto> photoDtos = user.getUserPhotos() != null
                ? user.getUserPhotos().stream()
                    .sorted((p1, p2) -> p1.getDisplayOrder().compareTo(p2.getDisplayOrder()))
                    .map(photo -> PhotoDto.builder()
                            .id(photo.getId())
                            .displayOrder(photo.getDisplayOrder())
                            .imageUrl(photo.getImageUrl())
                            .build())
                    .collect(Collectors.toList())
                : List.of();

        return UserPhotoUpdateResponse.builder()
                .photos(photoDtos)
                .build();
    }

    /**
     * 사용자 계정을 완전히 삭제 (Hard Delete)
     * User 엔티티와 연관된 모든 자식 엔티티들(Photo, Hobby, Preference, Lifestyle, DatingStyle, CoinBalance)도 함께 삭제
     * @Transactional을 통해 트랜잭션 안전성 보장
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Repository의 delete 메서드 호출
        // CascadeType.ALL과 orphanRemoval = true로 인해 자식 엔티티들도 함께 삭제됨
        userRepository.delete(user);
    }
}
