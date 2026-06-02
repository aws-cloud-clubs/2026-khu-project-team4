package love_cupid_crew.khunghap.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActiveUpdateRequest {

    @JsonProperty("is_active")
    @NotNull(message = "is_active는 필수입니다.")
    private Boolean isActive;
}

