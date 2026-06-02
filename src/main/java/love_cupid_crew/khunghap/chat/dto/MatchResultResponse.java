package love_cupid_crew.khunghap.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import love_cupid_crew.khunghap.chat.entity.ChatRoom;
import love_cupid_crew.khunghap.chat.entity.ChoiceReport;
import love_cupid_crew.khunghap.user.entity.User;

@Getter
@Builder
public class MatchResultResponse {

    @JsonProperty("room_id")
    private Long roomId;
    private PartnerDto partner;
    private String result;

    @Getter
    @Builder
    public static class PartnerDto {
        private String nickname;
        @JsonProperty("ilju_animal")
        private String iljuAnimal;
        @JsonProperty("ilju_emoji")
        private String iljuEmoji;
    }

    public static MatchResultResponse of(ChoiceReport cr, Long myId) {
        ChatRoom room = cr.getRoom();
        User partner = room.getUserA().getId().equals(myId)
                ? room.getUserB()
                : room.getUserA();

        return MatchResultResponse.builder()
                .roomId(room.getId())
                .partner(PartnerDto.builder()
                        .nickname(partner.getNickname())
                        .iljuAnimal(partner.getIljuAnimal().getName())
                        .iljuEmoji(partner.getIljuAnimal().getEmoji())
                        .build())
                .result(cr.getResult().name())
                .build();
    }
}
