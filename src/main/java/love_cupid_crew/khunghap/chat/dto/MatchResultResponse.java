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
        @JsonProperty("user_id")
        private Long userId;
        private String nickname;
        @JsonProperty("ilju_animal")
        private String iljuAnimal;
        @JsonProperty("ilju_emoji")
        private String iljuEmoji;
        @JsonProperty("ilju_image_url")
        private String iljuImageUrl;
    }

    public static MatchResultResponse of(ChoiceReport cr, Long myId) {
        ChatRoom room = cr.getRoom();
        User partner = room.getUserA().getId().equals(myId)
                ? room.getUserB()
                : room.getUserA();

        return MatchResultResponse.builder()
                .roomId(room.getId())
                .partner(PartnerDto.builder()
                        .userId(partner.getId())
                        .nickname(partner.getNickname())
                        .iljuAnimal(partner.getIljuAnimal().getName())
                        .iljuEmoji(partner.getIljuAnimal().getEmoji())
                        .iljuImageUrl(partner.getIljuAnimal().getImageUrl())
                        .build())
                .result(cr.getResult().name())
                .build();
    }
}
