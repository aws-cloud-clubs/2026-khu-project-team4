package love_cupid_crew.khunghap.explore;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import love_cupid_crew.khunghap.explore.dto.ExploreProfileSummaryResponse;
import love_cupid_crew.khunghap.user.entity.UserPreference;
import love_cupid_crew.khunghap.user.enums.PreferenceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExploreRepository {

    private final EntityManager em;

    public Page<ExploreProfileSummaryResponse> findCandidates(
            Long myId, List<UserPreference> hardExcludes, String myCollege, Pageable pageable) {

        boolean useCollegeFilter = myCollege != null &&
                hardExcludes.stream().anyMatch(p -> p.getCategory() == PreferenceCategory.SAME_COLLEGE);

        String from = buildFrom();
        String where = buildWhere(hardExcludes, useCollegeFilter);

        long total = executeCount(from + where, myId, useCollegeFilter, myCollege);
        List<ExploreProfileSummaryResponse> content = executeData(from + where, myId, useCollegeFilter, myCollege, pageable);

        return new PageImpl<>(content, pageable, total);
    }

    private String buildFrom() {
        return """
                FROM match_candidate mc
                JOIN "user" u ON u.id = mc.candidate_id
                JOIN ilju_animal ia ON ia.id = u.ilju_animal_id
                JOIN user_lifestyle ul ON ul.user_id = mc.candidate_id
                LEFT JOIN user_photo up ON up.user_id = mc.candidate_id AND up.display_order = 1
                """;
    }

    private String buildWhere(List<UserPreference> hardExcludes, boolean useCollegeFilter) {
        StringBuilder sb = new StringBuilder("""
                WHERE u.is_active = true
                AND mc.user_id = :myId
                AND mc.candidate_id NOT IN (
                    SELECT user_b_id FROM chat_room WHERE user_a_id = :myId
                    UNION
                    SELECT user_a_id FROM chat_room WHERE user_b_id = :myId
                )
                AND mc.candidate_id NOT IN (
                    SELECT reported_id FROM report WHERE reporter_id = :myId
                )
                """);

        for (UserPreference pref : hardExcludes) {
            switch (pref.getCategory()) {
                case SMOKING      -> sb.append("AND ul.smoking != 'SMOKER'\n");
                case DRINKING     -> sb.append("AND ul.drinking != 'OFTEN'\n");
                case EXERCISE     -> sb.append("AND ul.exercise != 'NEVER'\n");
                case SAME_COLLEGE -> { if (useCollegeFilter) sb.append("AND u.college != :myCollege\n"); }
            }
        }

        return sb.toString();
    }

    private long executeCount(String sql, Long myId, boolean useCollegeFilter, String myCollege) {
        Query q = em.createNativeQuery("SELECT COUNT(*) " + sql);
        q.setParameter("myId", myId);
        if (useCollegeFilter) q.setParameter("myCollege", myCollege);
        return ((Number) q.getSingleResult()).longValue();
    }

    private List<ExploreProfileSummaryResponse> executeData(
            String sql, Long myId, boolean useCollegeFilter, String myCollege, Pageable pageable) {

        String select = """
                SELECT u.id, u.nickname, u.birth_date, ia.name, ia.oheng,
                       mc.compatibility_score, mc.oheng_relation, ul.emoji_intro, up.image_url
                """;

        Query q = em.createNativeQuery(select + sql + "ORDER BY mc.compatibility_score DESC");
        q.setParameter("myId", myId);
        if (useCollegeFilter) q.setParameter("myCollege", myCollege);
        q.setFirstResult((int) pageable.getOffset());
        q.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Object[]> rows = q.getResultList();
        return rows.stream().map(this::mapRow).toList();
    }

    private ExploreProfileSummaryResponse mapRow(Object[] row) {
        return ExploreProfileSummaryResponse.builder()
                .userId(((Number) row[0]).longValue())
                .nickname((String) row[1])
                .age(AgeCalculator.calculateAge(((Date) row[2]).toLocalDate()))
                .iljuAnimal((String) row[3])
                .iljuOheng((String) row[4])
                .compatibilityScore(row[5] != null ? ((Number) row[5]).intValue() : null)
                .ohengRelation((String) row[6])
                .emojiIntro((String) row[7])
                .photoUrl((String) row[8])
                .build();
    }
}
