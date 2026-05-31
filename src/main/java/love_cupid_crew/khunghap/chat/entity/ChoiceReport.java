package love_cupid_crew.khunghap.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import love_cupid_crew.khunghap.chat.enums.ChoiceReportStatus;

import java.time.OffsetDateTime;

@Entity
@Table(name = "choice_report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ChoiceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false, unique = true)
    private ChatRoom room;

    @Column(name = "user_a_choice", length = 10)
    private String userAChoice;

    @Column(name = "user_b_choice", length = 10)
    private String userBChoice;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChoiceReportStatus result = ChoiceReportStatus.WAITING;

    @Column(name = "user_a_last_message", columnDefinition = "TEXT")
    private String userALastMessage;

    @Column(name = "user_b_last_message", columnDefinition = "TEXT")
    private String userBLastMessage;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime expiresAt;

    @Column
    private OffsetDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        expiresAt = createdAt.plusHours(24);
    }
}