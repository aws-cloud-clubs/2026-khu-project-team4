package love_cupid_crew.khunghap.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChoiceRespondedEvent {

    private final String type = "CHOICE_RESPONDED";

    @JsonProperty("room_id")
    private Long roomId;
}
