package com.sooscode.sooscode_api.application.code.controller;

import com.sooscode.sooscode_api.application.code.dto.CodeShareDto;
import com.sooscode.sooscode_api.global.config.websocket.WebSocketSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CodeController {

    private final WebSocketSessionRegistry sessionRegistry;

    /**
     * 코드 공유 메시지 처리
     * 클라이언트 → SEND /app/code/{classId}
     * 서버 → /topic/code/{classId} 로 브로드캐스트
     */
    @MessageMapping("/code/{classId}")
    @SendTo("/topic/code/{classId}")
    public CodeShareDto shareCode(
            @DestinationVariable Long classId,
            CodeShareDto dto,
            StompHeaderAccessor accessor
    ) {

        // WebSocket sessionId 조회
        String sessionId = accessor.getSessionId();
        Long userId = sessionRegistry.getUserId(sessionId);

        if (userId == null) {
            log.warn("Unauthorized user tried to send code (sessionId={})", sessionId);
            return null;
        }

        dto.setClassId(classId);
        dto.setUserId(userId);

        log.info("CODE SEND — classId={}, userId={}, language={}, length={}",
                classId, userId, dto.getLanguage(),
                dto.getCode() != null ? dto.getCode().length() : 0
        );

        return dto;
    }
}