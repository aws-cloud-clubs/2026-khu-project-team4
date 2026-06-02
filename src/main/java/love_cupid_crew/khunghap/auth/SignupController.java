package love_cupid_crew.khunghap.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.auth.dto.PresignedRequest;
import love_cupid_crew.khunghap.auth.dto.PresignedResponse;
import love_cupid_crew.khunghap.auth.dto.SignupRequest;
import love_cupid_crew.khunghap.auth.dto.SignupResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/signup")
@RequiredArgsConstructor
public class SignupController {

    private final SignupService signupService;

    @PostMapping("/photos/presigned")
    public ResponseEntity<PresignedResponse> getPresignedUrls(
            @Valid @RequestBody PresignedRequest request) {
        return ResponseEntity.ok(signupService.generatePresignedUrls(request.getCount()));
    }

    @PostMapping
    public ResponseEntity<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(signupService.signup(request));
    }
}