package love_cupid_crew.khunghap.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import love_cupid_crew.khunghap.user.enums.PreferenceCategory;
import love_cupid_crew.khunghap.user.enums.PreferenceScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenceDto {

    @JsonProperty("category")
    @NotNull(message = "preference.category는 필수입니다.")
    private PreferenceCategory category;

    @JsonProperty("score")
    @NotNull(message = "preference.score는 필수입니다.")
    private PreferenceScore score;
}

