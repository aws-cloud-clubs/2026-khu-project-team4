package love_cupid_crew.khunghap.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomDetailResponse {

    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("target_user")
    private TargetUserInfo targetUser;

    @JsonProperty("message_count")
    private int messageCount;

    private String status;

    @JsonProperty("compatibility_report")
    private String compatibilityReport;

    @JsonProperty("choice_status")
    private String choiceStatus;

    @JsonProperty("my_choice_submitted")
    private boolean myChoiceSubmitted;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

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
