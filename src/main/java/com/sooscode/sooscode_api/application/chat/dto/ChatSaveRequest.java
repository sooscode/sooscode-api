package com.sooscode.sooscode_api.application.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Setter
public class ChatSaveRequest {
    private Long chatId;
    private Long userId;
    private Long classId;
    private String content;
    private LocalDateTime createdAt;

}
