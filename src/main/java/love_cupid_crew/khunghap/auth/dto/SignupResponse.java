package love_cupid_crew.khunghap.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponse {

    private String accessToken;
    private String refreshToken;
    private UserInfo user;

    @Getter
    @Builder
    public static class UserInfo {
        private Long id;
        private String nickname;
        @JsonProperty("ilju_animal")
        private String iljuAnimal;
        @JsonProperty("ilju_oheng")
        private String iljuOheng;
        @JsonProperty("ilju_emoji")
        private String iljuEmoji;
        private int coins;
    }
}