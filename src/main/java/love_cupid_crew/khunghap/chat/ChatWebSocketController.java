package love_cupid_crew.khunghap.chat;

import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.chat.dto.ChatWebSocketRequest;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload ChatWebSocketRequest request,
            Principal principal) {
        Long senderId = Long.parseLong(principal.getName());
        chatService.sendMessage(senderId, roomId, request.getContent());
    }
}
