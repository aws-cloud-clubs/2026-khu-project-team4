package love_cupid_crew.khunghap.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.auth.dto.LoginRequest;
import love_cupid_crew.khunghap.auth.dto.RefreshRequest;
import love_cupid_crew.khunghap.auth.dto.RegisterRequest;
import love_cupid_crew.khunghap.auth.dto.SendCodeRequest;
import love_cupid_crew.khunghap.auth.dto.TokenResponse;
import love_cupid_crew.khunghap.auth.dto.VerifiedTokenResponse;
import love_cupid_crew.khunghap.auth.dto.VerifyCodeRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/email/send-code")
    public ResponseEntity<Void> sendCode(@Valid @RequestBody SendCodeRequest request) {
        emailVerificationService.sendCode(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email/verify-code")
    public ResponseEntity<VerifiedTokenResponse> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        String verifiedToken = emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(new VerifiedTokenResponse(verifiedToken));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }
}
