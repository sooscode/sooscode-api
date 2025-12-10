package com.sooscode.sooscode_api.application.chat.dto;

import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {
    private Long chatId;
    private Long classId;
    private Long userId;
    private String email;
    private String name;
    private String content;
    private LocalDateTime createdAt;



    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.getChatId(),
                message.getClassRoom().getClassId(),
                message.getUser() != null ? message.getUser().getUserId() : null,
                message.getUser() != null ? message.getUser().getEmail() : null,
                message.getUser() != null ? message.getUser().getName() : null,
                message.getContent(),
                message.getCreatedAt()
        );
    }
    public static ChatMessageResponse system( // 입퇴장용
            Long classId,
            Long userId,
            String email,
            String name,
            String content
    ) {
        return new ChatMessageResponse(
                null,
                classId,
                userId,
                email,
                name,
                content,
                LocalDateTime.now()
        );
    }

}
