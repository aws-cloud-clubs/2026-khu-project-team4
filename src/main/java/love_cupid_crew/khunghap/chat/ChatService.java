package love_cupid_crew.khunghap.chat;

import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.chat.dto.ChatRoomDetailResponse;
import love_cupid_crew.khunghap.chat.dto.ChoiceRequest;
import love_cupid_crew.khunghap.chat.dto.ChoiceRespondedEvent;
import love_cupid_crew.khunghap.chat.dto.ChoiceResponse;
import love_cupid_crew.khunghap.chat.dto.MessageLimitReachedEvent;
import love_cupid_crew.khunghap.chat.dto.MessageListResponse;
import love_cupid_crew.khunghap.chat.dto.NewMessageEvent;
import love_cupid_crew.khunghap.chat.dto.ChatRoomSummaryResponse;
import love_cupid_crew.khunghap.chat.dto.CreateChatRoomRequest;
import love_cupid_crew.khunghap.chat.dto.CreateChatRoomResponse;
import love_cupid_crew.khunghap.chat.entity.ChatMessage;
import love_cupid_crew.khunghap.chat.entity.ChatRoom;
import love_cupid_crew.khunghap.chat.entity.ChoiceReport;
import love_cupid_crew.khunghap.chat.enums.ChatRoomStatus;
import love_cupid_crew.khunghap.chat.enums.ChoiceReportStatus;
import love_cupid_crew.khunghap.chat.enums.UserChoice;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
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

import java.time.Duration;
import java.util.Collections;
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
    private final RedisPublisher redisPublisher;
    private final StringRedisTemplate stringRedisTemplate;

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
    public ChatRoomDetailResponse getChatRoomDetail(Long currentUserId, Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        boolean isUserA = room.getUserA().getId().equals(currentUserId);
        if (!isUserA && !room.getUserB().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        User targetUser = isUserA ? room.getUserB() : room.getUserA();

        String compatibilityReport = matchCandidateRepository
                .findByUser_IdAndCandidate_Id(currentUserId, targetUser.getId())
                .map(mc -> mc.getOhengRelation())
                .orElse(null);

        String choiceStatus = null;
        boolean myChoiceSubmitted = false;
        if (room.getMessageCount() >= 150) {
            ChoiceReport choiceReport = choiceReportRepository.findByRoom_Id(roomId).orElse(null);
            if (choiceReport != null) {
                choiceStatus = choiceReport.getResult().name();
                UserChoice myChoice = isUserA ? choiceReport.getUserAChoice() : choiceReport.getUserBChoice();
                myChoiceSubmitted = myChoice != UserChoice.WAITING;
            }
        }

        return ChatRoomDetailResponse.builder()
                .roomId(room.getId())
                .targetUser(ChatRoomDetailResponse.TargetUserInfo.builder()
                        .userId(targetUser.getId())
                        .nickname(targetUser.getNickname())
                        .iljuAnimal(targetUser.getIljuAnimal() != null ? targetUser.getIljuAnimal().getName() : null)
                        .build())
                .messageCount(room.getMessageCount())
                .status(room.getStatus().name())
                .compatibilityReport(compatibilityReport)
                .choiceStatus(choiceStatus)
                .myChoiceSubmitted(myChoiceSubmitted)
                .createdAt(room.getCreatedAt())
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

    @Transactional(readOnly = true)
    public MessageListResponse getMessages(Long currentUserId, Long roomId, Long before, int size) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        if (!room.getUserA().getId().equals(currentUserId) && !room.getUserB().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }

        PageRequest pageable = PageRequest.of(0, size + 1);
        List<ChatMessage> messages = before == null
                ? chatMessageRepository.findByRoom_IdOrderByIdDesc(roomId, pageable)
                : chatMessageRepository.findByRoom_IdAndIdLessThanOrderByIdDesc(roomId, before, pageable);

        boolean hasMore = messages.size() > size;
        if (hasMore) messages = messages.subList(0, size);

        Collections.reverse(messages);

        Long nextCursor = hasMore ? messages.get(0).getId() : null;

        List<MessageListResponse.MessageInfo> messageInfos = messages.stream()
                .map(m -> MessageListResponse.MessageInfo.builder()
                        .id(m.getId())
                        .senderId(m.getSender().getId())
                        .content(m.getContent())
                        .sentAt(m.getSentAt())
                        .build())
                .toList();

        return MessageListResponse.builder()
                .messages(messageInfos)
                .hasMore(hasMore)
                .nextCursor(nextCursor)
                .build();
    }

    @Transactional
    public void sendMessage(Long senderId, Long roomId, String content) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        boolean isUserA = room.getUserA().getId().equals(senderId);
        if (!isUserA && !room.getUserB().getId().equals(senderId)) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        if (room.getStatus() != ChatRoomStatus.ACTIVE) {
            throw new IllegalArgumentException("메시지를 보낼 수 없는 채팅방입니다.");
        }
        if (room.getMessageCount() >= 150) {
            throw new IllegalArgumentException("메시지 한도에 도달했습니다.");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ChatMessage message = chatMessageRepository.save(ChatMessage.builder()
                .room(room)
                .sender(sender)
                .content(content)
                .build());

        room.incrementMessageCount();
        chatRoomRepository.save(room);

        NewMessageEvent newMessageEvent = NewMessageEvent.builder()
                .roomId(roomId)
                .message(NewMessageEvent.MessageInfo.builder()
                        .id(message.getId())
                        .senderId(senderId)
                        .content(content)
                        .sentAt(message.getSentAt())
                        .build())
                .messageCount(room.getMessageCount())
                .build();

        redisPublisher.publish("chat:room:" + roomId, newMessageEvent);

        if (room.getMessageCount() == 150) {
            choiceReportRepository.save(ChoiceReport.builder().room(room).build());
            room.updateStatus(ChatRoomStatus.COMPLETED);
            stringRedisTemplate.opsForValue().set("choice:expire:" + roomId, "1", Duration.ofHours(24));

            redisPublisher.publish("chat:room:" + roomId,
                    MessageLimitReachedEvent.builder().roomId(roomId).build());
        }
    }

    public ChoiceResponse submitChoice(Long currentUserId, Long roomId, ChoiceRequest request) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        boolean isUserA = room.getUserA().getId().equals(currentUserId);
        if (!isUserA && !room.getUserB().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        if (room.getMessageCount() < 150) {
            throw new IllegalArgumentException("아직 선택을 제출할 수 없습니다.");
        }

        ChoiceReport choiceReport = choiceReportRepository.findByRoom_Id(roomId)
                .orElseThrow(() -> new IllegalArgumentException("선택 정보를 찾을 수 없습니다."));

        UserChoice myCurrentChoice = isUserA ? choiceReport.getUserAChoice() : choiceReport.getUserBChoice();
        if (myCurrentChoice != UserChoice.WAITING) {
            throw new IllegalArgumentException("이미 선택을 제출했습니다.");
        }

        UserChoice choice = request.getChoice();
        if (isUserA) {
            choiceReport.submitUserAChoice(choice, request.getLastMessage());
        } else {
            choiceReport.submitUserBChoice(choice, request.getLastMessage());
        }

        UserChoice userAChoice = choiceReport.getUserAChoice();
        UserChoice userBChoice = choiceReport.getUserBChoice();

        boolean bothSubmitted = userAChoice != UserChoice.WAITING && userBChoice != UserChoice.WAITING;
        if (bothSubmitted) {
            ChoiceReportStatus result = (userAChoice == UserChoice.YES && userBChoice == UserChoice.YES)
                    ? ChoiceReportStatus.REVEALED
                    : ChoiceReportStatus.REJECTED;
            choiceReport.resolve(result);
            room.updateStatus(ChatRoomStatus.EXPIRED);

            redisPublisher.publish("chat:room:" + roomId,
                    ChoiceRespondedEvent.builder().roomId(roomId).build());
        }

        return ChoiceResponse.builder()
                .choice(choice.name())
                .result(choiceReport.getResult().name())
                .myChoiceSubmitted(true)
                .build();
    }

    public void handleChoiceExpiry(Long roomId) {
        choiceReportRepository.findByRoom_Id(roomId).ifPresent(report -> {
            if (report.getResult() == ChoiceReportStatus.WAITING) {
                report.resolve(ChoiceReportStatus.REJECTED);
                report.getRoom().updateStatus(ChatRoomStatus.EXPIRED);
                redisPublisher.publish("chat:room:" + roomId,
                        ChoiceRespondedEvent.builder().roomId(roomId).build());
            }
        });
    }
}
