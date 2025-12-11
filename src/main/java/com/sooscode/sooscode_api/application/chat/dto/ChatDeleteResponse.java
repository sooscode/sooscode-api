package com.sooscode.sooscode_api.application.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatDeleteResponse {
    private Long chatId;
    private Long classId;
    private ChatMessageType type;   // DELETE
}
