// 내가(reporterId) 신고한 내역을 최신순(CreatedAtDesc)으로 조회하는 쿼리 메서드를 작성해 줘.
// 반환 타입은 List<Report> 이며, 메서드명은 List<Report> findAllByReporterIdOrderByCreatedAtDesc(Long reporterId) 야.

package love_cupid_crew.khunghap.report.repository;

import love_cupid_crew.khunghap.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByReporterIdOrderByCreatedAtDesc(Long reporterId);
}


