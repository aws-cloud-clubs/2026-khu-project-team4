package love_cupid_crew.khunghap.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import love_cupid_crew.khunghap.chat.enums.ChoiceReportStatus;
import love_cupid_crew.khunghap.user.entity.User;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChoiceReportStatus status = ChoiceReportStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String lastMessage;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime requestedAt;

    @Column
    private OffsetDateTime respondedAt;

    @PrePersist
    protected void onRequest() {
        requestedAt = OffsetDateTime.now();
    }
}
