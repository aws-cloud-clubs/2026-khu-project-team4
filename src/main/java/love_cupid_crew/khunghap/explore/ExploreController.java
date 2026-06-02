package love_cupid_crew.khunghap.explore;

import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.explore.dto.ExploreListResponse;
import love_cupid_crew.khunghap.explore.dto.ExploreProfileDetailResponse;
import love_cupid_crew.khunghap.global.security.CustomUserDetails;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/explore")
@RequiredArgsConstructor
public class ExploreController {

    private final ExploreService exploreService;

    @GetMapping
    public ResponseEntity<ExploreListResponse> getExploreList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ExploreListResponse response = exploreService.getExploreList(
                userDetails.getUserId(), PageRequest.of(page, size));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ExploreProfileDetailResponse> getProfileDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userId) {

        ExploreProfileDetailResponse response = exploreService.getProfileDetail(
                userDetails.getUserId(), userId);
        return ResponseEntity.ok(response);
    }
}
