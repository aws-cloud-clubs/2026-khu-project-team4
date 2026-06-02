package love_cupid_crew.khunghap.report.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import love_cupid_crew.khunghap.validation.ValidEnum;
import love_cupid_crew.khunghap.report.enums.ReportCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportCreateRequest {

	@JsonProperty("reported_id")
	@NotNull(message = "reported_id는 필수입니다.")
	private Long reportedId;

	@JsonProperty("category")
	@NotNull(message = "category는 필수입니다.")
	@ValidEnum(enumClass = ReportCategory.class, message = "category 값이 올바르지 않습니다.")
	private String category;

	@JsonProperty("reason")
	@NotNull(message = "reason은 필수입니다.")
	@Size(max = 1000, message = "reason은 최대 1000자까지 가능합니다.")
	private String reason;
}


