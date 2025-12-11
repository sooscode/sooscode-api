package com.sooscode.sooscode_api.application.chat.service;

import com.sooscode.sooscode_api.application.chat.dto.ChatMessageRequest;
import com.sooscode.sooscode_api.application.chat.dto.ChatMessageResponse;
import com.sooscode.sooscode_api.application.chat.dto.EnterOrExitResponse;
import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    
    // 채팅 저장
    ChatMessageResponse saveMessage(ChatMessageRequest request, Long userId);
    // 방별로 히스토리 조회
    List<ChatMessageResponse> findAllByClassRoom(Long classId);
    //메시지 삭제
    void deleteMessage(Long classId, Long chatId, Long userId);
    // 입장 로그 찍기
    EnterOrExitResponse enterchatRoom(Long userId, Long classRoomId);
    // 퇴장 로그 찍기
    EnterOrExitResponse exitchatRoom(Long userId, Long classRoomId);

}
