package love_cupid_crew.khunghap.report.service;

import love_cupid_crew.khunghap.report.dto.ReportCreateRequest;
import love_cupid_crew.khunghap.report.dto.ReportCreateResponse;
import love_cupid_crew.khunghap.report.dto.ReportResponseDto;
import love_cupid_crew.khunghap.report.entity.Report;
import love_cupid_crew.khunghap.report.enums.ReportCategory;
import love_cupid_crew.khunghap.report.enums.ReportStatus;
import love_cupid_crew.khunghap.report.repository.ReportRepository;
import love_cupid_crew.khunghap.user.entity.User;
import love_cupid_crew.khunghap.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

	private final ReportRepository reportRepository;
	private final UserRepository userRepository;

	@Transactional
	public ReportCreateResponse createReport(Long reporterId, ReportCreateRequest req) {
		if (reporterId == null) {
			throw new IllegalArgumentException("Reporter id is required");
		}

		User reporter = userRepository.findById(reporterId)
				.orElseThrow(() -> new IllegalArgumentException("Reporter not found with id: " + reporterId));

		User reported = userRepository.findById(req.getReportedId())
				.orElseThrow(() -> new IllegalArgumentException("Reported user not found with id: " + req.getReportedId()));

		if (reporter.getId().equals(reported.getId())) {
			throw new IllegalArgumentException("자기 자신을 신고할 수 없습니다.");
		}

		ReportCategory category;
		try {
			category = ReportCategory.valueOf(req.getCategory());
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid category: " + req.getCategory());
		}

		Report report = Report.builder()
				.reporter(reporter)
				.reported(reported)
				.category(category)
				.reason(req.getReason())
				.status(ReportStatus.PENDING)
				.build();

		Report saved = reportRepository.save(report);

		return ReportCreateResponse.builder()
				.reportId(saved.getId())
				.status(saved.getStatus())
				.build();
	}

	@Transactional(readOnly = true)
	public List<ReportResponseDto> getMyReports(Long reporterId) {
		List<Report> reports = reportRepository.findAllByReporterIdOrderByCreatedAtDesc(reporterId);

		return reports.stream()
				.map(report -> new ReportResponseDto(
						report.getId(),
						report.getReported().getNickname(),
						report.getCategory(),
						report.getStatus(),
                        report.getCreatedAt().toLocalDateTime()
				))
				.collect(Collectors.toList());
	}
}


