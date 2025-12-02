package com.sooscode.sooscode_api.domain.chatmessage.service;

import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService {

    ChatMessage saveMessage(ChatMessage chatMessage);

    List<ChatMessage> findAllByClassRoom_ClassIdOrderByCreatedAtAsc(Long classId);
}
