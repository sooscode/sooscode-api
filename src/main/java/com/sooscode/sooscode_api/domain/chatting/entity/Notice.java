package com.sooscode.sooscode_api.domain.chatting.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;   // 공지 제목

    @Column(columnDefinition = "TEXT")
    private String content; // 공지 내용

    private LocalDateTime createdAt;
}