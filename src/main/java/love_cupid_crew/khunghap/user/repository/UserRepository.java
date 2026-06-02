package love_cupid_crew.khunghap.user.repository;

import love_cupid_crew.khunghap.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    @Query("SELECT u.college FROM User u WHERE u.id = :id")
    Optional<String> findCollegeById(@Param("id") Long id);

    List<User> findByIsActiveTrueAndIdNot(Long id);
}
