package love_cupid_crew.khunghap.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import love_cupid_crew.khunghap.user.entity.User;

import java.time.OffsetDateTime;

@Entity
@Table(name = "chat_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime sentAt;

    @PrePersist
    protected void onSend() {
        sentAt = OffsetDateTime.now();
    }
}
