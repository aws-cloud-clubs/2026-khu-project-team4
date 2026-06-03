package love_cupid_crew.khunghap.ilju;

import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.ilju.dto.IljuAnimalDetailResponse;
import love_cupid_crew.khunghap.ilju.dto.IljuAnimalSummaryResponse;
import love_cupid_crew.khunghap.ilju.entity.IljuAnimal;
import love_cupid_crew.khunghap.ilju.repository.IljuAnimalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EncyclopediaService {

    private final IljuAnimalRepository iljuAnimalRepository;

    public List<IljuAnimalSummaryResponse> getAll() {
        return iljuAnimalRepository.findAll().stream()
                .map(IljuAnimalSummaryResponse::from)
                .toList();
    }

    public IljuAnimalDetailResponse getDetail(Long animalId) {
        IljuAnimal target = iljuAnimalRepository.findById(animalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 일주 동물입니다."));

        List<IljuAnimal> all = iljuAnimalRepository.findAll();
        return IljuAnimalDetailResponse.of(target, all);
    }
}
