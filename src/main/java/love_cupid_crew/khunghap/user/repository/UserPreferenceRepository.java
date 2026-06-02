package love_cupid_crew.khunghap.user.repository;

import love_cupid_crew.khunghap.user.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
}