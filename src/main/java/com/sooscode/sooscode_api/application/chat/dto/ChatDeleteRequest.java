package com.sooscode.sooscode_api.application.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatDeleteRequest {
    private Long chatId;   // 어떤 메시지를 지울지
}