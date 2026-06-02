package love_cupid_crew.khunghap.chat.repository;

import love_cupid_crew.khunghap.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Optional<ChatMessage> findTopByRoom_IdOrderBySentAtDesc(Long roomId);
}
