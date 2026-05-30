package love_cupid_crew.khunghap.auth;

import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.auth.dto.LoginRequest;
import love_cupid_crew.khunghap.auth.dto.RegisterRequest;
import love_cupid_crew.khunghap.auth.dto.TokenResponse;
import love_cupid_crew.khunghap.global.jwt.JwtProvider;
import love_cupid_crew.khunghap.global.security.CustomUserDetails;
import love_cupid_crew.khunghap.user.entity.User;
import love_cupid_crew.khunghap.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        CustomUserDetails userDetails = CustomUserDetails.from(user);
        return new TokenResponse(
                jwtProvider.generateAccessToken(userDetails),
                jwtProvider.generateRefreshToken(user.getId())
        );
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken) || jwtProvider.isAccessToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 refresh 토큰입니다.");
        }

        Long userId = jwtProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        CustomUserDetails userDetails = CustomUserDetails.from(user);
        return new TokenResponse(
                jwtProvider.generateAccessToken(userDetails),
                jwtProvider.generateRefreshToken(userId)  // token rotation
        );
    }
}
