package love_cupid_crew.khunghap.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import love_cupid_crew.khunghap.report.enums.ReportCategory;
import love_cupid_crew.khunghap.report.enums.ReportStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDto {

    @JsonProperty("report_id")
    private Long reportId;

    @JsonProperty("reported_nickname")
    private String reportedNickname;

    @JsonProperty("category")
    private ReportCategory category;

    @JsonProperty("status")
    private ReportStatus status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}