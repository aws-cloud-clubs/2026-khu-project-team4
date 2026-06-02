package love_cupid_crew.khunghap.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String nickname;
    private String iljuAnimal;
    private String iljuOheng;
}