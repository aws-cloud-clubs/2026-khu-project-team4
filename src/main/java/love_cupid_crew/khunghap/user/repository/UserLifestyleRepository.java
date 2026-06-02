package love_cupid_crew.khunghap.user.repository;

import love_cupid_crew.khunghap.user.entity.UserLifestyle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLifestyleRepository extends JpaRepository<UserLifestyle, Long> {
    Optional<UserLifestyle> findByUser_Id(Long userId);
}