package love_cupid_crew.khunghap.user.repository;

import love_cupid_crew.khunghap.user.entity.UserHobby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserHobbyRepository extends JpaRepository<UserHobby, Long> {
    List<UserHobby> findByUser_Id(Long userId);
}