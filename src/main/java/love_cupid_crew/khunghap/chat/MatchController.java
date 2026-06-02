package love_cupid_crew.khunghap.chat;

import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.chat.dto.MatchDetailResponse;
import love_cupid_crew.khunghap.chat.dto.MatchResultResponse;
import love_cupid_crew.khunghap.global.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/matches")
    public ResponseEntity<List<MatchResultResponse>> getMyMatches(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(matchService.getMyMatches(userDetails.getUserId()));
    }

    @GetMapping("/matches/{roomId}")
    public ResponseEntity<MatchDetailResponse> getMatchDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long roomId) {
        return ResponseEntity.ok(matchService.getMatchDetail(userDetails.getUserId(), roomId));
    }
}
