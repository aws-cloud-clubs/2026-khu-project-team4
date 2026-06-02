package love_cupid_crew.khunghap.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import love_cupid_crew.khunghap.validation.ValidEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileUpdateRequest {

    @JsonProperty("nickname")
    @Size(max = 50, message = "nickname은 최대 50자까지 가능합니다.")
    private String nickname;

    @JsonProperty("lifestyle")
    @Valid
    private LifestyleRequest lifestyle;

    @JsonProperty("hobbies")
    @Size(max = 20, message = "hobbies 최대 20개까지 가능합니다.")
    private List<@Size(max = 30, message = "취미는 최대 30자까지 가능합니다.") String> hobbies;

    @JsonProperty("dating_style")
    @Valid
    private DatingStyleRequest datingStyle;

    @JsonProperty("preferences")
    @Valid
    private List<PreferenceDto> preferences;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LifestyleRequest {
        @JsonProperty("emoji_intro")
        @Size(max = 50, message = "emoji_intro는 최대 50자까지 가능합니다.")
        private String emojiIntro;

        @JsonProperty("mbti")
        @Size(min = 4, max = 4, message = "mbti는 4글자여야 합니다.")
        @Pattern(regexp = "[A-Z]{4}", message = "mbti는 대문자 4자로 입력하세요.")
        private String mbti;

        @JsonProperty("drinking")
        @ValidEnum(enumClass = love_cupid_crew.khunghap.user.enums.DrinkingHabit.class, message = "drinking 값이 올바르지 않습니다.")
        private String drinking; // use String to allow enum mapping manually

        @JsonProperty("smoking")
        @ValidEnum(enumClass = love_cupid_crew.khunghap.user.enums.SmokingHabit.class, message = "smoking 값이 올바르지 않습니다.")
        private String smoking;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DatingStyleRequest {
        @JsonProperty("purpose")
        @ValidEnum(enumClass = love_cupid_crew.khunghap.user.enums.DatingPurpose.class, message = "purpose 값이 올바르지 않습니다.")
        private String purpose;

        @JsonProperty("contact_style")
        @ValidEnum(enumClass = love_cupid_crew.khunghap.user.enums.ContactStyle.class, message = "contact_style 값이 올바르지 않습니다.")
        private String contactStyle;

        @JsonProperty("date_style")
        @ValidEnum(enumClass = love_cupid_crew.khunghap.user.enums.DateStyle.class, message = "date_style 값이 올바르지 않습니다.")
        private String dateStyle;

        @JsonProperty("personal_time")
        @ValidEnum(enumClass = love_cupid_crew.khunghap.user.enums.PersonalTime.class, message = "personal_time 값이 올바르지 않습니다.")
        private String personalTime;

        @JsonProperty("age_min")
        @Min(value = -100, message = "age_min 범위를 벗어났습니다.")
        @Max(value = 100, message = "age_min 범위를 벗어났습니다.")
        private Short ageMin;

        @JsonProperty("age_max")
        @Min(value = -100, message = "age_max 범위를 벗어났습니다.")
        @Max(value = 100, message = "age_max 범위를 벗어났습니다.")
        private Short ageMax;

        @JsonProperty("height_min")
        @Min(value = 100, message = "height_min 범위를 벗어났습니다.")
        @Max(value = 250, message = "height_min 범위를 벗어났습니다.")
        private Short heightMin;

        @JsonProperty("height_max")
        @Min(value = 100, message = "height_max 범위를 벗어났습니다.")
        @Max(value = 250, message = "height_max 범위를 벗어났습니다.")
        private Short heightMax;

        @JsonProperty("same_college_excluded")
        private boolean sameCollegeExcluded;
    }
}

