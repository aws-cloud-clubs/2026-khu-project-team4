package love_cupid_crew.khunghap.user.entity;

import jakarta.persistence.*;
import lombok.*;
import love_cupid_crew.khunghap.user.enums.DrinkingHabit;
import love_cupid_crew.khunghap.user.enums.ExerciseHabit;
import love_cupid_crew.khunghap.user.enums.SmokingHabit;

@Entity
@Table(name = "user_lifestyle")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class UserLifestyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 50)
    private String emojiIntro;

    @Column(length = 4)
    private String mbti;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DrinkingHabit drinking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SmokingHabit smoking;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExerciseHabit exercise;

    // ===== 업데이트 헬퍼 메서드 (JPA 더티체킹용) =====
    public void setUser(User user) {
        this.user = user;
    }

    public void update(String emojiIntro, String mbti, DrinkingHabit drinking, SmokingHabit smoking) {
        this.emojiIntro = emojiIntro;
        this.mbti = mbti;
        this.drinking = drinking;
        this.smoking = smoking;
    }
}
