package love_cupid_crew.khunghap.report.entity;

import jakarta.persistence.*;
import lombok.*;
import love_cupid_crew.khunghap.report.enums.ReportCategory;
import love_cupid_crew.khunghap.report.enums.ReportStatus;
import love_cupid_crew.khunghap.user.entity.User;

import java.time.OffsetDateTime;

@Entity
@Table(name = "report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id", nullable = false)
    private User reported;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportCategory category;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (status == null) status = ReportStatus.PENDING;
        createdAt = OffsetDateTime.now();
    }
}

