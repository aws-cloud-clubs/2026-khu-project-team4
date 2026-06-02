package love_cupid_crew.khunghap.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import love_cupid_crew.khunghap.user.enums.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank
    private String verifiedToken;

    @NotBlank
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String nickname;

    @NotBlank
    private String phone;

    @NotNull
    @JsonProperty("birth_date")
    private LocalDate birthDate;

    @JsonProperty("birth_hour")
    private BirthHour birthHour;

    @NotNull
    private Gender gender;

    @NotBlank
    private String college;

    @NotNull
    @Valid
    private LifestyleRequest lifestyle;

    @NotEmpty
    private List<String> hobbies;

    @NotNull
    @Valid
    @JsonProperty("dating_style")
    private DatingStyleRequest datingStyle;

    @NotEmpty
    private List<@Valid PreferenceRequest> preferences;

    @NotNull
    @Valid
    private TermsRequest terms;

    @NotEmpty
    @Size(max = 5, message = "사진은 최대 5장까지 등록 가능합니다.")
    private List<@Valid PhotoRequest> photos;

    @Getter
    @NoArgsConstructor
    public static class LifestyleRequest {
        @JsonProperty("emoji_intro")
        private String emojiIntro;
        private String mbti;
        @NotNull
        private DrinkingHabit drinking;
        @NotNull
        private SmokingHabit smoking;
        @NotNull
        private ExerciseHabit exercise;
    }

    @Getter
    @NoArgsConstructor
    public static class DatingStyleRequest {
        @NotNull
        private DatingPurpose purpose;
        @NotNull
        @JsonProperty("contact_style")
        private ContactStyle contactStyle;
        @NotNull
        @JsonProperty("date_style")
        private DateStyle dateStyle;
        @NotNull
        @JsonProperty("personal_time")
        private PersonalTime personalTime;
        @JsonProperty("age_min")
        private Short ageMin;
        @JsonProperty("age_max")
        private Short ageMax;
        @JsonProperty("height_min")
        private Short heightMin;
        @JsonProperty("height_max")
        private Short heightMax;
    }

    @Getter
    @NoArgsConstructor
    public static class PreferenceRequest {
        @NotNull
        private PreferenceCategory category;
        @NotNull
        private PreferenceScore score;
    }

    @Getter
    @NoArgsConstructor
    public static class TermsRequest {
        @JsonProperty("age_verified")
        private boolean ageVerified;
        @JsonProperty("service_agreed")
        private boolean serviceAgreed;
        @JsonProperty("privacy_agreed")
        private boolean privacyAgreed;
        @JsonProperty("marketing_agreed")
        private boolean marketingAgreed;
    }

    @Getter
    @NoArgsConstructor
    public static class PhotoRequest {
        @NotBlank
        @JsonProperty("temp_key")
        private String tempKey;
        @NotNull
        @JsonProperty("display_order")
        private Short displayOrder;
    }
}