package love_cupid_crew.khunghap.explore;

import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.explore.dto.ExploreListResponse;
import love_cupid_crew.khunghap.explore.dto.ExploreProfileDetailResponse;
import love_cupid_crew.khunghap.explore.dto.ExploreProfileSummaryResponse;
import love_cupid_crew.khunghap.match.entity.MatchCandidate;
import love_cupid_crew.khunghap.match.repository.MatchCandidateRepository;
import love_cupid_crew.khunghap.user.entity.*;
import love_cupid_crew.khunghap.user.enums.PreferenceCategory;
import love_cupid_crew.khunghap.user.enums.PreferenceScore;
import love_cupid_crew.khunghap.user.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExploreService {

    private final ExploreRepository exploreRepository;
    private final UserRepository userRepository;
    private final UserPreferenceRepository preferenceRepository;
    private final UserLifestyleRepository lifestyleRepository;
    private final UserHobbyRepository hobbyRepository;
    private final UserDatingStyleRepository datingStyleRepository;
    private final UserPhotoRepository photoRepository;
    private final MatchCandidateRepository matchCandidateRepository;

    public ExploreListResponse getExploreList(Long myId, Pageable pageable) {
        List<UserPreference> hardExcludes = preferenceRepository.findByUser_Id(myId).stream()
                .filter(p -> p.getScore() == PreferenceScore.VERY_BAD)
                .toList();

        boolean excludeSameCollege = hardExcludes.stream()
                .anyMatch(p -> p.getCategory() == PreferenceCategory.SAME_COLLEGE);
        String myCollege = excludeSameCollege
                ? userRepository.findCollegeById(myId).orElse(null)
                : null;

        Page<ExploreProfileSummaryResponse> page =
                exploreRepository.findCandidates(myId, hardExcludes, myCollege, pageable);

        return ExploreListResponse.from(page);
    }

    public ExploreProfileDetailResponse getProfileDetail(Long myId, Long targetUserId) {
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));

        MatchCandidate mc = matchCandidateRepository
                .findByUserIdAndCandidateId(myId, targetUserId)
                .orElse(null);

        UserLifestyle lifestyle = lifestyleRepository.findByUser_Id(targetUserId).orElse(null);
        List<UserHobby> hobbies = hobbyRepository.findByUser_Id(targetUserId);
        UserDatingStyle datingStyle = datingStyleRepository.findByUser_Id(targetUserId).orElse(null);
        List<UserPreference> preferences = preferenceRepository.findByUser_Id(targetUserId);
        List<UserPhoto> photos = photoRepository.findByUser_IdOrderByDisplayOrderAsc(targetUserId);

        return ExploreProfileDetailResponse.of(target, mc, lifestyle, hobbies, datingStyle, preferences, photos);
    }
}
