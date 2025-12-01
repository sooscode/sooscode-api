package com.sooscode.sooscode_api.domain.chatting.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "chat_room_member",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"chat_room_id", "nickname"})
        }
)
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 어떤 방에 속해있는지 (FK: chat_room.id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    // ✅ 방에 참여한 닉네임
    @Column(nullable = false, length = 50)
    private String nickname;

    // ✅ 참여한 시각
    private LocalDateTime joinedAt;

    @PrePersist
    public void onPrePersist() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
    }
}
