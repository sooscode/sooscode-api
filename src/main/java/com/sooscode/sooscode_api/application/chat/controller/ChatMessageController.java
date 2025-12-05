package com.sooscode.sooscode_api.application.chat.controller;

import com.sooscode.sooscode_api.application.chat.dto.ChatMessageResponse;
import com.sooscode.sooscode_api.application.chat.dto.ChatMessageRequest;
import com.sooscode.sooscode_api.application.chat.service.ChatMessageService;
import com.sooscode.sooscode_api.global.websocket.WebSocketSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@CrossOrigin(origins = { "http://localhost:5173", "http://10.41.0.89:5173" })
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatMessageController {

    private final WebSocketSessionRegistry sessionRegistry;
    private final ChatMessageService chatMessageServiceService;


    /**
     * 코드 공유 메시지 처리
     * 클라이언트 → SEND /app/code/{classId}
     * 서버 → /topic/code/{classId} 로 브로드캐스트
     */
    @MessageMapping("/chat/{classId}")
    @SendTo("/topic/chat/{classId}")
    public ChatMessageRequest chatMessage(
            @DestinationVariable Long classId,
            ChatMessageRequest request,
            StompHeaderAccessor accessor
    ) {

        // WebSocket sessionId 조회
        String sessionId = accessor.getSessionId();
        Long userId = sessionRegistry.getUserId(sessionId);

        if (userId == null) {
            log.warn("Unauthorized user tried to send code (sessionId={})", sessionId);
            return null;
        }

        request.setClassId(classId);
        request.setUserId(userId);
        request.setCreatedAt(LocalDateTime.now());

        log.info("CODE SEND — classId={}, userId={}, createdAt={}, length={}",
                classId, userId, request.getCreatedAt(),
                request.getContent() != null ? request.getContent().length() : 0
        );
        chatMessageServiceService.saveMessage(request, userId);
        return request;
    }

    @GetMapping("/history")
    public List<ChatMessageResponse> findAllByClassRoom_ClassIdOrderByCreatedAtAsc(@RequestParam("classId") Long classId) {
        return chatMessageServiceService.findAllByClassRoom(classId);
    }

    private void effectiveness(){

    }

}
