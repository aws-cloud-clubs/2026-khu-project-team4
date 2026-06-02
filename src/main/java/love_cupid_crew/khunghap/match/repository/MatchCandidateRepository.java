package love_cupid_crew.khunghap.match.repository;

import love_cupid_crew.khunghap.match.entity.MatchCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MatchCandidateRepository extends JpaRepository<MatchCandidate, Long> {
    Optional<MatchCandidate> findByUser_IdAndCandidate_Id(Long userId, Long candidateId);

    @Query("SELECT mc FROM MatchCandidate mc WHERE mc.user.id = :userId AND mc.candidate.id = :candidateId")
    Optional<MatchCandidate> findByUserIdAndCandidateId(@Param("userId") Long userId, @Param("candidateId") Long candidateId);
}