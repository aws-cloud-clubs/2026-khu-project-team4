package love_cupid_crew.khunghap.explore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class ExploreListResponse {

    private List<ExploreProfileSummaryResponse> content;
    @JsonProperty("total_elements")
    private long totalElements;
    @JsonProperty("total_pages")
    private int totalPages;
    @JsonProperty("current_page")
    private int currentPage;
    @JsonProperty("has_next")
    private boolean hasNext;

    public static ExploreListResponse from(Page<ExploreProfileSummaryResponse> page) {
        return ExploreListResponse.builder()
                .content(page.getContent())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .hasNext(page.hasNext())
                .build();
    }
}