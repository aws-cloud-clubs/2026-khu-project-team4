package love_cupid_crew.khunghap.user.entity;

import jakarta.persistence.*;
import lombok.*;
import love_cupid_crew.khunghap.user.enums.ContactStyle;
import love_cupid_crew.khunghap.user.enums.DateStyle;
import love_cupid_crew.khunghap.user.enums.DatingPurpose;
import love_cupid_crew.khunghap.user.enums.PersonalTime;

@Entity
@Table(name = "user_dating_style")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class UserDatingStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DatingPurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContactStyle contactStyle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DateStyle dateStyle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PersonalTime personalTime;

    @Column
    private Short ageMin;

    @Column
    private Short ageMax;

    @Column
    private Short heightMin;

    @Column
    private Short heightMax;
}
