package love_cupid_crew.khunghap.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_photo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class UserPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    private Short displayOrder;

    // ===== 업데이트 헬퍼 메서드 =====
    public void setUser(User user) {
        this.user = user;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDisplayOrder(Short displayOrder) {
        this.displayOrder = displayOrder;
    }
}
