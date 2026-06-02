package love_cupid_crew.khunghap.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("display_order")
    private Short displayOrder;

    @JsonProperty("blur_url")
    private String imageUrl;
}

