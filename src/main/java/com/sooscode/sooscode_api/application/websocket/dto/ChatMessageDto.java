package com.sooscode.sooscode_api.application.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long classId;
    private Long userId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
    private MessageType type;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}