package love_cupid_crew.khunghap.user.entity;

import jakarta.persistence.*;
import lombok.*;
import love_cupid_crew.khunghap.user.enums.PreferenceCategory;
import love_cupid_crew.khunghap.user.enums.PreferenceScore;

@Entity
@Table(name = "user_preference")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PreferenceCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PreferenceScore score;

    // ===== 업데이트 헬퍼 메서드 =====
    public void setUser(User user) {
        this.user = user;
    }

    public void setCategory(PreferenceCategory category) {
        this.category = category;
    }

    public void setScore(PreferenceScore score) {
        this.score = score;
    }
}
