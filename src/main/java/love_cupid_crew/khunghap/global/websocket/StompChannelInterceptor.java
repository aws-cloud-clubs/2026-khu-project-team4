package love_cupid_crew.khunghap.global.websocket;

import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.global.jwt.JwtProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("WebSocket 인증 토큰이 없습니다.");
            }
            String token = authHeader.substring(7);
            if (!jwtProvider.validateToken(token) || !jwtProvider.isAccessToken(token)) {
                throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
            }
            Long userId = jwtProvider.getUserId(token);
            accessor.setUser(() -> String.valueOf(userId));
        }

        return message;
    }
}
