package love_cupid_crew.khunghap.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import love_cupid_crew.khunghap.user.enums.DrinkingHabit;
import love_cupid_crew.khunghap.user.enums.SmokingHabit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LifestyleDto {

    @JsonProperty("emoji_intro")
    private String emojiIntro;

    @JsonProperty("mbti")
    private String mbti;

    @JsonProperty("drinking")
    private DrinkingHabit drinking;

    @JsonProperty("smoking")
    private SmokingHabit smoking;
}

