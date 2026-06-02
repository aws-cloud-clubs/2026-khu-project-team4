package love_cupid_crew.khunghap.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_hobby")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class UserHobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 30)
    private String hobby;

    // ===== 업데이트 헬퍼 메서드 =====
    public void setUser(User user) {
        this.user = user;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }
}
