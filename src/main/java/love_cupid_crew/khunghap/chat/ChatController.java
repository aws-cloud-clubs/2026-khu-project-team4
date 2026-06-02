package love_cupid_crew.khunghap.chat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.chat.dto.CreateChatRoomRequest;
import love_cupid_crew.khunghap.chat.dto.CreateChatRoomResponse;
import love_cupid_crew.khunghap.global.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<CreateChatRoomResponse> createChatRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateChatRoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatService.createChatRoom(userDetails.getUserId(), request));
    }
}
