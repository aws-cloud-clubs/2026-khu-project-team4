package love_cupid_crew.khunghap.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import love_cupid_crew.khunghap.user.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("birth_date")
    private String birthDate;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("college")
    private String college;

    @JsonProperty("ilju_animal")
    private String iljuAnimal;

    @JsonProperty("ilju_oheng")
    private String iljuOheng;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("balance")
    private int balance;

    @JsonProperty("photos")
    private List<PhotoDto> photos;

    @JsonProperty("lifestyle")
    private LifestyleDto lifestyle;

    @JsonProperty("hobbies")
    private List<String> hobbies;

    @JsonProperty("dating_style")
    private DatingStyleDto datingStyle;

    @JsonProperty("preferences")
    private List<PreferenceDto> preferences;
}

