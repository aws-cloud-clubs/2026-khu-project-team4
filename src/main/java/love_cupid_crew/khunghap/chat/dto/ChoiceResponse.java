package love_cupid_crew.khunghap.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChoiceResponse {

    private String choice;

    private String result;

    @JsonProperty("my_choice_submitted")
    private boolean myChoiceSubmitted;
}
