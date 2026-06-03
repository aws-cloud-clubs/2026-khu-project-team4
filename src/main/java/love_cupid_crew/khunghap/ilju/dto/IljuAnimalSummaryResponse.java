package love_cupid_crew.khunghap.ilju.dto;

import lombok.Builder;
import lombok.Getter;
import love_cupid_crew.khunghap.ilju.entity.IljuAnimal;

@Getter
@Builder
public class IljuAnimalSummaryResponse {

    private Long id;
    private String name;
    private String emoji;
    private String oheng;
    private String cheongan;
    private String jiji;
    private String imageUrl;

    public static IljuAnimalSummaryResponse from(IljuAnimal animal) {
        return IljuAnimalSummaryResponse.builder()
                .id(animal.getId())
                .name(animal.getName())
                .emoji(animal.getEmoji())
                .oheng(animal.getOheng())
                .cheongan(animal.getCheongan())
                .jiji(animal.getJiji())
                .imageUrl(animal.getImageUrl())
                .build();
    }
}
