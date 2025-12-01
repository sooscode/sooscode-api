package com.sooscode.sooscode_api.domain.chatmessage.entity;

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
                @UniqueConstraint(columnNames = {"message_id", "reactor"})
        }
)
public class ChatMessageReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 메시지에 대한 반응인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private ChatMessage message;

    // 누가 눌렀는지 (닉네임)
    @Column(nullable = false, length = 50)
    private String reactor;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
