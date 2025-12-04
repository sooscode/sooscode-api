package com.sooscode.sooscode_api.domain.chatmessage.entity;

import com.sooscode.sooscode_api.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "chat_message_reaction",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"message_id", "user_id"})
        }
)
public class ChatMessageReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 어떤 메시지에 대한 반응인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private ChatMessage message;

    // ✅ 누가 눌렀는지 (User 엔티티 연결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
