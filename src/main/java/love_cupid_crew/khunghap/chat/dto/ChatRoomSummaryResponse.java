package love_cupid_crew.khunghap.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomSummaryResponse {

    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("target_user")
    private TargetUserInfo targetUser;

    @JsonProperty("last_message")
    private String lastMessage;

    @JsonProperty("last_message_at")
    private OffsetDateTime lastMessageAt;

    @JsonProperty("message_count")
    private int messageCount;

    private String status;

    @JsonProperty("choice_status")
    private String choiceStatus;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TargetUserInfo {

        @JsonProperty("user_id")
        private Long userId;

        private String nickname;

        @JsonProperty("ilju_animal")
        private String iljuAnimal;
    }
}
