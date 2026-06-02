package love_cupid_crew.khunghap.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import love_cupid_crew.khunghap.chat.enums.UserChoice;

@Getter
public class ChoiceRequest {

    @NotNull
    private UserChoice choice;

    @JsonProperty("last_message")
    private String lastMessage;
}
