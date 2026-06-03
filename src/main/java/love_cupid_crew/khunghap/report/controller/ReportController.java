package love_cupid_crew.khunghap.report.controller;

import jakarta.validation.Valid;
import love_cupid_crew.khunghap.global.security.CustomUserDetails;
import love_cupid_crew.khunghap.report.dto.ReportCreateRequest;
import love_cupid_crew.khunghap.report.dto.ReportCreateResponse;
import love_cupid_crew.khunghap.report.dto.ReportResponseDto;
import love_cupid_crew.khunghap.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@PostMapping
	public ResponseEntity<ReportCreateResponse> createReport(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@Valid @RequestBody ReportCreateRequest req) {
		ReportCreateResponse resp = reportService.createReport(userDetails.getUserId(), req);
		return ResponseEntity.status(201).body(resp);
	}

	@GetMapping("/me")
	public ResponseEntity<List<ReportResponseDto>> getMyReports(
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		List<ReportResponseDto> response = reportService.getMyReports(userDetails.getUserId());
		return ResponseEntity.ok(response);
	}
}


