package com.sooscode.sooscode_api.application.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatReactionMessage {

    private Long chatId;
    private int reactionCount;
    private Long classId;
    private ChatMessageType type;

}
