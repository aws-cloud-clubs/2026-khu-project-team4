package love_cupid_crew.khunghap.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PresignedResponse {

    private List<PhotoUrl> photos;

    @Getter
    @Builder
    public static class PhotoUrl {
        private String presignedUrl;
        private String tempKey;
        private int expiresIn;
    }
}