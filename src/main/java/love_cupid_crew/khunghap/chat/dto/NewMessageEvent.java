package love_cupid_crew.khunghap.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
@AllArgsConstructor
public class NewMessageEvent {

    private final String type = "NEW_MESSAGE";

    @JsonProperty("room_id")
    private Long roomId;

    private MessageInfo message;

    @JsonProperty("message_count")
    private int messageCount;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MessageInfo {
        private Long id;

        @JsonProperty("sender_id")
        private Long senderId;

        private String content;

        @JsonProperty("sent_at")
        private OffsetDateTime sentAt;
    }
}
