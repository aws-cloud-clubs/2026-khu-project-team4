package love_cupid_crew.khunghap.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MessageListResponse {

    private List<MessageInfo> messages;

    @JsonProperty("has_more")
    private boolean hasMore;

    @JsonProperty("next_cursor")
    private Long nextCursor;

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
