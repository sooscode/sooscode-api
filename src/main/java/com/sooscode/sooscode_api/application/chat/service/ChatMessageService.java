package com.sooscode.sooscode_api.application.chat.service;

import com.sooscode.sooscode_api.application.chat.dto.ChatMessageRequest;
import com.sooscode.sooscode_api.application.chat.dto.ChatMessageResponse;
import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    
    //채팅 저장
    ChatMessageResponse saveMessage(ChatMessageRequest request, Long userId);
    // 방별로 히스토리 조회
    List<ChatMessageResponse> findAllByClassRoom(Long classId);
}
