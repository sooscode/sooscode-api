package com.sooscode.sooscode_api.application.chat.controller;

import com.sooscode.sooscode_api.application.chat.dto.ChatDto;
import com.sooscode.sooscode_api.application.chat.service.ChatService;
import com.sooscode.sooscode_api.infra.websocket.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * 채팅 WebSocket 컨트롤러
 *
 * 메시지 전송: /app/chat/{classId}/send
 * 메시지 삭제: /app/chat/{classId}/delete
 * 리액션:     /app/chat/{classId}/reaction
 * 타이핑:     /app/chat/{classId}/typing
 *
 * 구독:
 * - /topic/class/{classId}/chat    → 채팅 메시지
 * - /topic/class/{classId}/typing  → 타이핑 상태
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SessionService sessionService;
    private final ChatService chatService;

    /**
     * 채팅 메시지 전송
     */
    @MessageMapping("/chat/{classId}/send")
    public void sendMessage(
            @DestinationVariable Long classId,
            @Payload ChatDto.SendRequest request,
            StompHeaderAccessor accessor
    ) {
        String sessionId = accessor.getSessionId();
        Long userId = sessionService.getUserId(sessionId);
        String username = sessionService.getUsername(sessionId);

        if (userId == null) {
            log.warn("인증되지 않은 사용자의 채팅 시도");
            return;
        }

        chatService.sendMessage(classId, userId, username, request);
    }

    /**
     * 채팅 메시지 삭제
     */
    @MessageMapping("/chat/{classId}/delete")
    public void deleteMessage(
            @DestinationVariable Long classId,
            @Payload ChatDto.DeleteRequest request,
            StompHeaderAccessor accessor
    ) {
        String sessionId = accessor.getSessionId();
        Long userId = sessionService.getUserId(sessionId);

        if (userId == null) {
            log.warn("인증되지 않은 사용자의 삭제 시도");
            return;
        }

        chatService.deleteMessage(classId, request.getChatId(), userId);
    }

    /**
     * 리액션 토글
     */
    @MessageMapping("/chat/{classId}/reaction")
    public void toggleReaction(
            @DestinationVariable Long classId,
            @Payload ChatDto.ReactionRequest request,
            StompHeaderAccessor accessor
    ) {
        String sessionId = accessor.getSessionId();
        Long userId = sessionService.getUserId(sessionId);

        if (userId == null) {
            log.warn("인증되지 않은 사용자의 리액션 시도");
            return;
        }

        chatService.toggleReaction(classId, request.getChatId(), userId);
    }

    /**
     * 타이핑 상태 전송
     */
    @MessageMapping("/chat/{classId}/typing")
    public void typing(
            @DestinationVariable Long classId,
            @Payload ChatDto.TypingRequest request,
            StompHeaderAccessor accessor
    ) {
        String sessionId = accessor.getSessionId();
        Long userId = sessionService.getUserId(sessionId);
        String username = sessionService.getUsername(sessionId);

        if (userId == null) {
            return;
        }

        chatService.broadcastTyping(classId, userId, username, request.isTyping());
    }
}