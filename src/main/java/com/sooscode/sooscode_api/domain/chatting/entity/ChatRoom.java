package com.sooscode.sooscode_api.domain.chatting.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;              // 1, 2, 3, 4 ... 방 번호

    private LocalDateTime createdAt;
}