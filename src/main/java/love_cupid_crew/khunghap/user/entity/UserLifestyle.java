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
}
