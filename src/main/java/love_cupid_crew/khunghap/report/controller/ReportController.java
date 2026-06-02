package love_cupid_crew.khunghap.report.controller;

import jakarta.validation.Valid;
import love_cupid_crew.khunghap.report.dto.ReportCreateRequest;
import love_cupid_crew.khunghap.report.dto.ReportCreateResponse;
import love_cupid_crew.khunghap.report.dto.ReportResponseDto;
import love_cupid_crew.khunghap.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@PostMapping
	public ResponseEntity<ReportCreateResponse> createReport(@Valid @RequestBody ReportCreateRequest req) {
		Long reporterId = 1L; // TODO: replace with authenticated user id
		ReportCreateResponse resp = reportService.createReport(reporterId, req);
		return ResponseEntity.status(201).body(resp);
	}

	@GetMapping("/me")
	public ResponseEntity<List<ReportResponseDto>> getMyReports() {
		// 현재 인증 기능이 없으므로 임시로 1번 유저를 신고자(본인)로 가정하고 하드코딩
		Long currentUserId = 1L;
		List<ReportResponseDto> response = reportService.getMyReports(currentUserId);
		return ResponseEntity.ok(response);
	}
}


