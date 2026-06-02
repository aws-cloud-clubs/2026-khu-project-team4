package love_cupid_crew.khunghap.auth.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PresignedRequest {

    @NotNull
    @Min(1)
    @Max(5)
    private Integer count;
}