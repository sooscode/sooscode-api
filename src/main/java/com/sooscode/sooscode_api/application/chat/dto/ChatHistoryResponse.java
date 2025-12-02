package com.sooscode.sooscode_api.application.chat.dto;

import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatHistoryResponse {
    private Long chatId;
    private Long userId;
    private Long classId;
    private String content;
    private LocalDateTime createdAt;

    public static ChatHistoryResponse from(ChatMessage message) {
        return new ChatHistoryResponse(
                message.getChatId(),
                message.getUser() != null ? message.getUser().getUser_id() : null,
                message.getClassRoom().getClassId(),
                message.getContent(),
                message.getCreatedAt()
        );
    }

}
