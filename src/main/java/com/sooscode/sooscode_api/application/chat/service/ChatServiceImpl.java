package com.sooscode.sooscode_api.application.chat.service;

import com.sooscode.sooscode_api.application.chat.dto.ChatHistoryResponse;
import com.sooscode.sooscode_api.application.chat.dto.ChatSaveRequest;
import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessage;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageService chatMessageService;
    private final ClassRoomRepository classRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void saveAndBroadcast(ChatSaveRequest chatSaveRequest) {

        Long classId = chatSaveRequest.getClassId();

        // 1) classId → ClassRoom 엔티티(또는 프록시)로 변환
        ClassRoom classRoom = classRoomRepository.getReferenceById(classId);
        // 조회 쿼리 아끼고 싶으면 getReferenceById, 안정적으로 하려면 findById(...).orElseThrow(...)

        // 2) ChatMessage 엔티티 생성 (지금은 user 안 쓰니까 null)
        ChatMessage message = ChatMessage.of(
                null,                        // User 나중에 로그인 붙일 때 넣자
                classRoom,
                chatSaveRequest.getContent()
        );

        // 3) 저장
        ChatMessage saved = chatMessageService.saveMessage(message);

        // 4) 응답용 DTO로 변환
        ChatHistoryResponse response = ChatHistoryResponse.from(saved);

        // 5) /topic/chat/{classId} 로 브로드캐스트
        String destination = "/topic/chat/" + classId;
        messagingTemplate.convertAndSend(destination, response);
    }

    @Override
    public List<ChatHistoryResponse> getHistoryByClassRoom_ClassIdOrderByCreatedAtAsc(Long classId) {
        return chatMessageService
                .findAllByClassRoom_ClassIdOrderByCreatedAtAsc(classId)
                .stream()
                .map(ChatHistoryResponse::from)
                .toList();
    }
}
