package love_cupid_crew.khunghap.chat;

import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.chat.dto.ChatRoomSummaryResponse;
import love_cupid_crew.khunghap.chat.dto.CreateChatRoomRequest;
import love_cupid_crew.khunghap.chat.dto.CreateChatRoomResponse;
import love_cupid_crew.khunghap.chat.entity.ChatMessage;
import love_cupid_crew.khunghap.chat.entity.ChatRoom;
import love_cupid_crew.khunghap.chat.entity.ChoiceReport;
import love_cupid_crew.khunghap.chat.repository.ChatMessageRepository;
import love_cupid_crew.khunghap.chat.repository.ChatRoomRepository;
import love_cupid_crew.khunghap.chat.repository.ChoiceReportRepository;
import love_cupid_crew.khunghap.match.repository.MatchCandidateRepository;
import love_cupid_crew.khunghap.user.entity.CoinBalance;
import love_cupid_crew.khunghap.user.entity.User;
import love_cupid_crew.khunghap.user.repository.CoinBalanceRepository;
import love_cupid_crew.khunghap.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChoiceReportRepository choiceReportRepository;
    private final CoinBalanceRepository coinBalanceRepository;
    private final UserRepository userRepository;
    private final MatchCandidateRepository matchCandidateRepository;

    public CreateChatRoomResponse createChatRoom(Long currentUserId, CreateChatRoomRequest request) {
        Long targetUserId = request.getTargetUserId();

        if (currentUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("자기 자신과 채팅방을 만들 수 없습니다.");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("대상 사용자를 찾을 수 없습니다."));

        CoinBalance coinBalance = coinBalanceRepository.findByUser_Id(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("코인 정보를 찾을 수 없습니다."));
        coinBalance.deduct(1);

        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .userA(currentUser)
                .userB(targetUser)
                .build());

        String compatibilityReport = matchCandidateRepository
                .findByUser_IdAndCandidate_Id(currentUserId, targetUserId)
                .map(mc -> mc.getOhengRelation())
                .orElse(null);

        return CreateChatRoomResponse.builder()
                .roomId(chatRoom.getId())
                .targetUser(CreateChatRoomResponse.TargetUserInfo.builder()
                        .userId(targetUser.getId())
                        .nickname(targetUser.getNickname())
                        .iljuAnimal(targetUser.getIljuAnimal() != null ? targetUser.getIljuAnimal().getName() : null)
                        .build())
                .messageCount(chatRoom.getMessageCount())
                .status(chatRoom.getStatus().name())
                .compatibilityReport(compatibilityReport)
                .remainingCoins(coinBalance.getBalance())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ChatRoomSummaryResponse> getChatRooms(Long currentUserId) {
        return chatRoomRepository.findByUserA_IdOrUserB_Id(currentUserId, currentUserId)
                .stream()
                .map(room -> {
                    User targetUser = room.getUserA().getId().equals(currentUserId)
                            ? room.getUserB() : room.getUserA();

                    ChatMessage lastMessage = chatMessageRepository
                            .findTopByRoom_IdOrderBySentAtDesc(room.getId())
                            .orElse(null);

                    String choiceStatus = null;
                    if (room.getMessageCount() >= 150) {
                        choiceStatus = choiceReportRepository.findByRoom_Id(room.getId())
                                .map(cr -> cr.getResult().name())
                                .orElse(null);
                    }

                    return ChatRoomSummaryResponse.builder()
                            .roomId(room.getId())
                            .targetUser(ChatRoomSummaryResponse.TargetUserInfo.builder()
                                    .userId(targetUser.getId())
                                    .nickname(targetUser.getNickname())
                                    .iljuAnimal(targetUser.getIljuAnimal() != null ? targetUser.getIljuAnimal().getName() : null)
                                    .build())
                            .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                            .lastMessageAt(lastMessage != null ? lastMessage.getSentAt() : null)
                            .messageCount(room.getMessageCount())
                            .status(room.getStatus().name())
                            .choiceStatus(choiceStatus)
                            .build();
                })
                .toList();
    }
}
