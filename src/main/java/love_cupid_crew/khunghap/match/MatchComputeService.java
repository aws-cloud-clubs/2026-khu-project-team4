package love_cupid_crew.khunghap.match;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import love_cupid_crew.khunghap.ilju.CompatibilityCalculator;
import love_cupid_crew.khunghap.match.entity.MatchCandidate;
import love_cupid_crew.khunghap.match.repository.MatchCandidateRepository;
import love_cupid_crew.khunghap.user.entity.User;
import love_cupid_crew.khunghap.user.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchComputeService {

    private final UserRepository userRepository;
    private final MatchCandidateRepository matchCandidateRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void computeMatchCandidates(UserSignedUpEvent event) {
        computeMatchCandidates(event.userId());
    }

    @Transactional
    public void computeMatchCandidates(Long newUserId) {
        User newUser = userRepository.findById(newUserId).orElse(null);
        if (newUser == null) {
            log.warn("MatchCandidate 계산 실패: 유저 미존재 (id={})", newUserId);
            return;
        }

        List<User> existingUsers = userRepository.findByIsActiveTrueAndIdNot(newUserId);
        if (existingUsers.isEmpty()) return;

        List<MatchCandidate> candidates = new ArrayList<>();
        for (User existing : existingUsers) {
            CompatibilityCalculator.Result result =
                    CompatibilityCalculator.calculate(newUser.getIljuAnimal(), existing.getIljuAnimal());

            candidates.add(MatchCandidate.builder()
                    .user(newUser)
                    .candidate(existing)
                    .compatibilityScore(result.score())
                    .ohengRelation(result.ohengRelation())
                    .build());

            candidates.add(MatchCandidate.builder()
                    .user(existing)
                    .candidate(newUser)
                    .compatibilityScore(result.score())
                    .ohengRelation(result.ohengRelation())
                    .build());
        }

        matchCandidateRepository.saveAll(candidates);
        log.info("MatchCandidate 계산 완료: userId={}, 대상={}명", newUserId, existingUsers.size());
    }
}
