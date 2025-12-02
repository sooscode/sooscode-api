package com.sooscode.sooscode_api.application.chat.service;

import com.sooscode.sooscode_api.application.chat.dto.ChatHistoryResponse;
import com.sooscode.sooscode_api.application.chat.dto.ChatSaveRequest;

import java.util.List;

public interface ChatService {
    void saveAndBroadcast(ChatSaveRequest chatSaveRequest);
    List<ChatHistoryResponse> getHistoryByClassRoom_ClassIdOrderByCreatedAtAsc(Long classId);

}
