package love_cupid_crew.khunghap.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChatWebSocketRequest {

    @NotBlank
    private String content;
}
