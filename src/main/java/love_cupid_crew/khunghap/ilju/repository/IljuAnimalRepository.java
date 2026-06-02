package love_cupid_crew.khunghap.ilju.repository;

import love_cupid_crew.khunghap.ilju.entity.IljuAnimal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IljuAnimalRepository extends JpaRepository<IljuAnimal, Long> {
    Optional<IljuAnimal> findByCheonganAndJiji(String cheongan, String jiji);
}