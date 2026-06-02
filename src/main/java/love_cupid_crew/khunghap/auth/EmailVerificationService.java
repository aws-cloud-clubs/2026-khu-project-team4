package love_cupid_crew.khunghap.auth;

import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.global.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    private final JwtProvider jwtProvider;

    @Value("${email.code-expiration}")
    private long codeExpirationSeconds;

    private static final String CODE_PREFIX     = "email:code:";
    private static final String VERIFIED_PREFIX = "email:verified:";

    @Value("${email.verified-expiration}")
    private long verifiedExpirationSeconds;

    public void sendCode(String email) {
        String code = generateCode();
        redisTemplate.delete(VERIFIED_PREFIX + email);
        redisTemplate.opsForValue().set(CODE_PREFIX + email, code, codeExpirationSeconds, TimeUnit.SECONDS);
        sendEmail(email, code);
    }

    public String verifyCode(String email, String code) {
        String stored = redisTemplate.opsForValue().get(CODE_PREFIX + email);

        if (stored == null) {
            throw new IllegalArgumentException("인증 코드가 만료되었습니다.");
        }
        if (!stored.equals(code)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않습니다.");
        }

        redisTemplate.delete(CODE_PREFIX + email);
        redisTemplate.opsForValue().set(VERIFIED_PREFIX + email, "1", verifiedExpirationSeconds, TimeUnit.SECONDS);
        return jwtProvider.generateVerifiedToken(email);
    }

    private String generateCode() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }

    private void sendEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[쿵합] 이메일 인증 코드");
        message.setText("인증 코드: " + code + "\n\n3분 내에 입력해주세요.");
        mailSender.send(message);
    }
}