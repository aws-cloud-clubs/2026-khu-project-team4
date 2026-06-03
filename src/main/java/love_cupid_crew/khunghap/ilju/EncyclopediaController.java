package love_cupid_crew.khunghap.ilju;

import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.ilju.dto.IljuAnimalDetailResponse;
import love_cupid_crew.khunghap.ilju.dto.IljuAnimalSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/encyclopedia")
@RequiredArgsConstructor
public class EncyclopediaController {

    private final EncyclopediaService encyclopediaService;

    @GetMapping
    public ResponseEntity<List<IljuAnimalSummaryResponse>> getAll() {
        return ResponseEntity.ok(encyclopediaService.getAll());
    }

    @GetMapping("/{animalId}")
    public ResponseEntity<IljuAnimalDetailResponse> getDetail(@PathVariable Long animalId) {
        return ResponseEntity.ok(encyclopediaService.getDetail(animalId));
    }
}
