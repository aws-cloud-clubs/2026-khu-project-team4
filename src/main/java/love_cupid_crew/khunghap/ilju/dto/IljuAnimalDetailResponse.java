package love_cupid_crew.khunghap.ilju.dto;

import lombok.Builder;
import lombok.Getter;
import love_cupid_crew.khunghap.ilju.CompatibilityCalculator;
import love_cupid_crew.khunghap.ilju.entity.IljuAnimal;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
public class IljuAnimalDetailResponse {

    private Long id;
    private String name;
    private String emoji;
    private String oheng;
    private String cheongan;
    private String jiji;
    private String description;
    private List<String> personality;
    private List<String> strengths;
    private List<String> weaknesses;
    private CompatibilityDto compatibility;

    @Getter
    @Builder
    public static class CompatibilityDto {
        private List<String> best;
        private List<String> good;
        private List<String> avoid;
    }

    public static IljuAnimalDetailResponse of(IljuAnimal target, List<IljuAnimal> all) {
        List<String> ranked = all.stream()
                .filter(o -> !o.getId().equals(target.getId()))
                .sorted((a, b) -> Short.compare(
                        CompatibilityCalculator.calculate(target, b).score(),
                        CompatibilityCalculator.calculate(target, a).score()))
                .map(IljuAnimal::getName)
                .toList();

        int size = ranked.size();
        List<String> best  = ranked.subList(0, Math.min(2, size));
        List<String> good  = ranked.subList(Math.min(2, size), Math.min(4, size));
        List<String> avoid = size >= 2 ? ranked.subList(size - 2, size) : List.of();

        return IljuAnimalDetailResponse.builder()
                .id(target.getId())
                .name(target.getName())
                .emoji(target.getEmoji())
                .oheng(target.getOheng())
                .cheongan(target.getCheongan())
                .jiji(target.getJiji())
                .description(target.getDescription())
                .personality(target.getPersonality() != null ? Arrays.asList(target.getPersonality()) : List.of())
                .strengths(target.getStrengths() != null ? Arrays.asList(target.getStrengths()) : List.of())
                .weaknesses(target.getWeaknesses() != null ? Arrays.asList(target.getWeaknesses()) : List.of())
                .compatibility(CompatibilityDto.builder()
                        .best(best)
                        .good(good)
                        .avoid(avoid)
                        .build())
                .build();
    }
}
