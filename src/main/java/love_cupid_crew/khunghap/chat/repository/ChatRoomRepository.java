package love_cupid_crew.khunghap.chat.repository;

import love_cupid_crew.khunghap.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByUserA_IdOrUserB_Id(Long userAId, Long userBId);
}
