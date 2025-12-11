package com.sooscode.sooscode_api.domain.chatmessage.entity;

import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private ClassRoom classRoom;

    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    //  답장 대상(부모 메시지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_chat_id")
    private ChatMessage reply;

    //  나에게 달린 답장들 (읽기용)
    /*@OneToMany(mappedBy = "parent")
    private List<ChatMessage> replies = new ArrayList<>();*/


    // ChatMessage 엔티티 내부
    public static ChatMessage of(User user, ClassRoom classRoom, String content, ChatMessage reply) {
        return ChatMessage.builder()
                .user(user)
                .classRoom(classRoom)
                .content(content)
                .reply(reply)
                .build();
    }

    public void markDeleted() {
        this.deleted = true;
        this.content = "삭제된 메시지입니다.";
    }

}
