package com.sooscode.sooscode_api.domain.chatting.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;

    @Column(columnDefinition = "TEXT")
    private String text;

    private LocalDateTime createdAt;

    // 🔥 방 구분용 필드 추가 (예: "1", "2", "soccer", "baseball" 아무거나)
    private String room;
}
