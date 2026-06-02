package love_cupid_crew.khunghap.user.repository;

import love_cupid_crew.khunghap.user.entity.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {
    List<UserPhoto> findByUser_IdOrderByDisplayOrderAsc(Long userId);
}