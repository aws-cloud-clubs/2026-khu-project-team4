package love_cupid_crew.khunghap.chat;

import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.chat.dto.MatchDetailResponse;
import love_cupid_crew.khunghap.chat.dto.MatchResultResponse;
import love_cupid_crew.khunghap.chat.entity.ChatRoom;
import love_cupid_crew.khunghap.chat.entity.ChoiceReport;
import love_cupid_crew.khunghap.chat.repository.ChoiceReportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    public MatchDetailResponse getMatchDetail(Long myId, Long roomId) {
        ChoiceReport cr = choiceReportRepository.findByRoomId(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 매칭입니다."));

        ChatRoom room = cr.getRoom();
        if (!room.getUserA().getId().equals(myId) && !room.getUserB().getId().equals(myId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 매칭입니다.");
        }

        return MatchDetailResponse.of(cr, myId);
    }
}
