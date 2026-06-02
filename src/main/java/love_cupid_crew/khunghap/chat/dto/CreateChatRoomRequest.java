package love_cupid_crew.khunghap.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateChatRoomRequest {

    @NotNull
    @JsonProperty("target_user_id")
    private Long targetUserId;
}
