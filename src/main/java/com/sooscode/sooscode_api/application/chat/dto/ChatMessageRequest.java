package com.sooscode.sooscode_api.application.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Setter
public class ChatMessageRequest {
    private Long classId;
    private String content;
    private Long replyToChatId; // 답장안하는 채팅일경우 null
}