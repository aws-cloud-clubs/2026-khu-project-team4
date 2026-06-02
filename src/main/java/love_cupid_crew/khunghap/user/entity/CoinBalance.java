package love_cupid_crew.khunghap.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "coin_balance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class CoinBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @Column(nullable = false)
    private int balance = 0;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    public void deduct(int amount) {
        if (this.balance < amount) {
            throw new IllegalArgumentException("코인이 부족합니다.");
        }
        this.balance -= amount;
    }

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
