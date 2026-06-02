package love_cupid_crew.khunghap.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendCodeRequest {

    @NotBlank
    @Email
    @Pattern(regexp = "^[\\w.+\\-]+@khu\\.ac\\.kr$", message = "경희대 이메일(@khu.ac.kr)만 허용됩니다.")
    private String email;
}