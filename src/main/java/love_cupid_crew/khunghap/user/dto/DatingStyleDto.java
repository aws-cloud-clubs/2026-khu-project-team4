package love_cupid_crew.khunghap.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import love_cupid_crew.khunghap.user.enums.ContactStyle;
import love_cupid_crew.khunghap.user.enums.DateStyle;
import love_cupid_crew.khunghap.user.enums.DatingPurpose;
import love_cupid_crew.khunghap.user.enums.PersonalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatingStyleDto {

    @JsonProperty("purpose")
    private DatingPurpose purpose;

    @JsonProperty("contact_style")
    private ContactStyle contactStyle;

    @JsonProperty("date_style")
    private DateStyle dateStyle;

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

    @JsonProperty("same_college_excluded")
    private boolean sameCollegeExcluded;
}

