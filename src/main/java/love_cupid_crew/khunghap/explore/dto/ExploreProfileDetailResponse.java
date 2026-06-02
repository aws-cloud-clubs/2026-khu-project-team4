package love_cupid_crew.khunghap.explore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import love_cupid_crew.khunghap.explore.AgeCalculator;
import love_cupid_crew.khunghap.match.entity.MatchCandidate;
import love_cupid_crew.khunghap.user.entity.*;

import java.util.List;

@Getter
@Builder
public class ExploreProfileDetailResponse {

    @JsonProperty("user_id")
    private Long userId;
    private String nickname;
    private int age;
    @JsonProperty("ilju_animal")
    private String iljuAnimal;
    @JsonProperty("ilju_oheng")
    private String iljuOheng;
    @JsonProperty("compatibility_score")
    private Integer compatibilityScore;
    @JsonProperty("oheng_relation")
    private String ohengRelation;
    @JsonProperty("emoji_intro")
    private String emojiIntro;
    private String mbti;
    private List<String> hobbies;
    private LifestyleDto lifestyle;
    @JsonProperty("dating_style")
    private DatingStyleDto datingStyle;
    private List<PreferenceDto> preferences;
    private List<PhotoDto> photos;

    @Getter
    @Builder
    public static class LifestyleDto {
        private String drinking;
        private String smoking;
        private String exercise;
    }

    @Getter
    @Builder
    public static class DatingStyleDto {
        private String purpose;
        @JsonProperty("contact_style")
        private String contactStyle;
        @JsonProperty("date_style")
        private String dateStyle;
        @JsonProperty("personal_time")
        private String personalTime;
    }

    @Getter
    @Builder
    public static class PreferenceDto {
        private String category;
        private String score;
    }

    @Getter
    @Builder
    public static class PhotoDto {
        @JsonProperty("display_order")
        private Short displayOrder;
        @JsonProperty("photo_url")
        private String photoUrl;
    }

    public static ExploreProfileDetailResponse of(
            User user, MatchCandidate mc, UserLifestyle lifestyle,
            List<UserHobby> hobbies, UserDatingStyle datingStyle,
            List<UserPreference> preferences, List<UserPhoto> photos) {

        return ExploreProfileDetailResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .age(AgeCalculator.calculateAge(user.getBirthDate()))
                .iljuAnimal(user.getIljuAnimal().getName())
                .iljuOheng(user.getIljuAnimal().getOheng())
                .compatibilityScore(mc != null ? (int) mc.getCompatibilityScore() : null)
                .ohengRelation(mc != null ? mc.getOhengRelation() : null)
                .emojiIntro(lifestyle.getEmojiIntro())
                .mbti(lifestyle.getMbti())
                .hobbies(hobbies.stream().map(UserHobby::getHobby).toList())
                .lifestyle(LifestyleDto.builder()
                        .drinking(lifestyle.getDrinking().name())
                        .smoking(lifestyle.getSmoking().name())
                        .exercise(lifestyle.getExercise().name())
                        .build())
                .datingStyle(DatingStyleDto.builder()
                        .purpose(datingStyle.getPurpose().name())
                        .contactStyle(datingStyle.getContactStyle().name())
                        .dateStyle(datingStyle.getDateStyle().name())
                        .personalTime(datingStyle.getPersonalTime().name())
                        .build())
                .preferences(preferences.stream()
                        .map(p -> PreferenceDto.builder()
                                .category(p.getCategory().name())
                                .score(p.getScore().name())
                                .build())
                        .toList())
                .photos(photos.stream()
                        .map(p -> PhotoDto.builder()
                                .displayOrder(p.getDisplayOrder())
                                .photoUrl(p.getImageUrl())
                                .build())
                        .toList())
                .build();
    }
}
