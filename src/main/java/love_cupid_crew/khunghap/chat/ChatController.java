package love_cupid_crew.khunghap.chat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.chat.dto.ChatRoomDetailResponse;
import love_cupid_crew.khunghap.chat.dto.ChatRoomSummaryResponse;
import love_cupid_crew.khunghap.chat.dto.MessageListResponse;
import love_cupid_crew.khunghap.chat.dto.CreateChatRoomRequest;
import love_cupid_crew.khunghap.chat.dto.CreateChatRoomResponse;
import love_cupid_crew.khunghap.global.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<ChatRoomSummaryResponse>> getChatRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(chatService.getChatRooms(userDetails.getUserId()));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomDetailResponse> getChatRoomDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getChatRoomDetail(userDetails.getUserId(), roomId));
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<MessageListResponse> getMessages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long roomId,
            @RequestParam(required = false) Long before,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(chatService.getMessages(userDetails.getUserId(), roomId, before, size));
    }
}
