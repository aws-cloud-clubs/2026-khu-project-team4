package love_cupid_crew.khunghap.ilju;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.ilju.entity.IljuAnimal;
import love_cupid_crew.khunghap.ilju.repository.IljuAnimalRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IljuAnimalDataInitializer implements ApplicationRunner {

    private final IljuAnimalRepository iljuAnimalRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ClassPathResource resource = new ClassPathResource("data/ilju-animals.json");
        List<IljuAnimalDto> dtos = Arrays.asList(
                objectMapper.readValue(resource.getInputStream(), IljuAnimalDto[].class)
        );

        if (iljuAnimalRepository.count() > 0) return;

        dtos.forEach(dto -> iljuAnimalRepository.save(IljuAnimal.builder()
                .name(dto.name())
                .emoji(dto.emoji())
                .cheongan(dto.cheongan())
                .jiji(dto.jiji())
                .oheng(dto.oheng())
                .description(dto.description())
                .personality(dto.personality())
                .strengths(dto.strengths())
                .weaknesses(dto.weaknesses())
                .imageUrl(dto.imageUrl())
                .build()));
    }

    private record IljuAnimalDto(
            String name,
            String emoji,
            String cheongan,
            String jiji,
            String oheng,
            String description,
            String[] personality,
            String[] strengths,
            String[] weaknesses,
            String imageUrl
    ) {}
}