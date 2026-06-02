package love_cupid_crew.khunghap.chat;

import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.chat.dto.MatchResultResponse;
import love_cupid_crew.khunghap.chat.repository.ChoiceReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private final ChoiceReportRepository choiceReportRepository;

    public List<MatchResultResponse> getMyMatches(Long myId) {
        return choiceReportRepository.findAllByUserId(myId).stream()
                .map(cr -> MatchResultResponse.of(cr, myId))
                .toList();
    }
}
