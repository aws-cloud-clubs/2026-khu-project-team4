package love_cupid_crew.khunghap.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import love_cupid_crew.khunghap.chat.entity.ChatRoom;
import love_cupid_crew.khunghap.chat.entity.ChoiceReport;
import love_cupid_crew.khunghap.chat.enums.ChoiceReportStatus;
import love_cupid_crew.khunghap.user.entity.User;

import java.time.OffsetDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchDetailResponse {

    private String result;
    @JsonProperty("expires_at")
    private OffsetDateTime expiresAt;
    private PartnerDto partner;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PartnerDto {
        private String nickname;
        private String name;
        private String phone;
        @JsonProperty("ilju_emoji")
        private String iljuEmoji;
        @JsonProperty("last_message")
        private String lastMessage;
    }

    public static MatchDetailResponse of(ChoiceReport cr, Long myId) {
        ChatRoom room = cr.getRoom();
        boolean iAmA = room.getUserA().getId().equals(myId);
        User partner = iAmA ? room.getUserB() : room.getUserA();
        String lastMessage = iAmA ? cr.getUserBLastMessage() : cr.getUserALastMessage();

        ChoiceReportStatus status = cr.getResult();

        PartnerDto.PartnerDtoBuilder partnerBuilder = PartnerDto.builder()
                .nickname(partner.getNickname())
                .iljuEmoji(partner.getIljuAnimal().getEmoji())
                .lastMessage(lastMessage);

        if (status == ChoiceReportStatus.REVEALED) {
            partnerBuilder.name(partner.getName()).phone(partner.getPhone());
        }

        MatchDetailResponse.MatchDetailResponseBuilder builder = MatchDetailResponse.builder()
                .result(status.name())
                .partner(partnerBuilder.build());

        if (status == ChoiceReportStatus.REVEALED) {
            builder.expiresAt(cr.getExpiresAt());
        }

        return builder.build();
    }
}
