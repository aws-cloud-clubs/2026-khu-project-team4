package love_cupid_crew.khunghap.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ChoiceExpiryListener implements MessageListener {

    private final ChatService chatService;

    private static final String PREFIX = "choice:expire:";

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody(), StandardCharsets.UTF_8);
        if (expiredKey.startsWith(PREFIX)) {
            Long roomId = Long.parseLong(expiredKey.substring(PREFIX.length()));
            chatService.handleChoiceExpiry(roomId);
        }
    }
}
