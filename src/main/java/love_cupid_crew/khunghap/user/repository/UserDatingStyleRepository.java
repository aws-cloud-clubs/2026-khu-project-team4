package love_cupid_crew.khunghap.user.repository;

import love_cupid_crew.khunghap.user.entity.UserDatingStyle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDatingStyleRepository extends JpaRepository<UserDatingStyle, Long> {
    Optional<UserDatingStyle> findByUser_Id(Long userId);
}