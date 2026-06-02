package love_cupid_crew.khunghap.explore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExploreProfileSummaryResponse {

    @JsonProperty("user_id")
    private Long userId;
    private String nickname;
    private int age;
    @JsonProperty("ilju_animal")
    private String iljuAnimal;
    @JsonProperty("ilju_oheng")
    private String iljuOheng;
    @JsonProperty("compatibility_score")
    private Integer compatibilityScore;
    @JsonProperty("oheng_relation")
    private String ohengRelation;
    @JsonProperty("emoji_intro")
    private String emojiIntro;
    @JsonProperty("photo_url")
    private String photoUrl;
}