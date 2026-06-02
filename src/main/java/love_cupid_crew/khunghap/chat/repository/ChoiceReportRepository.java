package love_cupid_crew.khunghap.chat.repository;

import love_cupid_crew.khunghap.chat.entity.ChoiceReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChoiceReportRepository extends JpaRepository<ChoiceReport, Long> {
    Optional<ChoiceReport> findByRoom_Id(Long roomId);
}
