package love_cupid_crew.khunghap.chat.repository;

import love_cupid_crew.khunghap.chat.entity.ChoiceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChoiceReportRepository extends JpaRepository<ChoiceReport, Long> {
    Optional<ChoiceReport> findByRoom_Id(Long roomId);
  
    @Query("""
            SELECT cr FROM ChoiceReport cr
            JOIN FETCH cr.room r
            JOIN FETCH r.userA ua JOIN FETCH ua.iljuAnimal
            JOIN FETCH r.userB ub JOIN FETCH ub.iljuAnimal
            WHERE ua.id = :myId OR ub.id = :myId
            """)
    List<ChoiceReport> findAllByUserId(@Param("myId") Long myId);
}
