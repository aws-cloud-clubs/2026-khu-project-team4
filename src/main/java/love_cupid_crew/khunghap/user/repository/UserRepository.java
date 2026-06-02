package love_cupid_crew.khunghap.user.repository;

import love_cupid_crew.khunghap.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
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

    /**
     * N+1 문제 해결을 위해 단일 쿼리로 User의 모든 연관 데이터를 함께 조회
     * JOIN FETCH를 사용하여 UserLifestyle, UserDatingStyle, UserPhoto, UserHobby, UserPreference를 한 번에 가져옴
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.userLifestyle " +
           "LEFT JOIN FETCH u.userDatingStyle " +
           "LEFT JOIN FETCH u.userPhotos " +
           "LEFT JOIN FETCH u.userHobbies " +
           "LEFT JOIN FETCH u.userPreferences " +
           "WHERE u.id = :userId")
    Optional<User> findByIdWithDetails(@Param("userId") Long userId);
}
