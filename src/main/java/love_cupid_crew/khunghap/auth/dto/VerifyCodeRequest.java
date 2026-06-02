package love_cupid_crew.khunghap.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyCodeRequest {

    @NotBlank
    @Email
    @Pattern(regexp = "^[\\w.+\\-]+@khu\\.ac\\.kr$", message = "경희대 이메일(@khu.ac.kr)만 허용됩니다.")
    private String email;

    @NotBlank
    @Size(min = 6, max = 6, message = "인증 코드는 6자리여야 합니다.")
    private String code;
}