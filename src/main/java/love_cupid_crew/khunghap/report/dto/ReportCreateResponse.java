package love_cupid_crew.khunghap.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import love_cupid_crew.khunghap.report.enums.ReportStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportCreateResponse {

	@JsonProperty("report_id")
	private Long reportId;

	@JsonProperty("status")
	private ReportStatus status;
}


