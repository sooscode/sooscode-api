package com.sooscode.sooscode_api.application.classroom.controller;

import com.sooscode.sooscode_api.application.classroom.dto.ClassModeMessage;
import com.sooscode.sooscode_api.infra.websocket.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ClassModeController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SessionService sessionService;

    /**
     * 수업 모드 변경
     * - 강사만 변경 가능
     * - /app/class/{classId}/mode 로 전송
     * - /topic/class/{classId}/mode 로 브로드캐스트
     */
    @MessageMapping("/class/{classId}/mode")
    public void changeMode(
            @DestinationVariable Long classId,
            ClassModeMessage message,
            StompHeaderAccessor accessor
    ) {
        String sessionId = accessor.getSessionId();
        Long userId = sessionService.getUserId(sessionId);

        // 인증 확인
        if (userId == null) {
            log.warn("인증되지 않은 사용자의 모드 변경 시도");
            return;
        }

        // 강사 권한 확인
        if (!sessionService.isInstructor(sessionId)) {
            log.warn("학생이 모드 변경 시도: userId={}", userId);
            return;
        }

        // 브로드캐스트
        messagingTemplate.convertAndSend(
                "/topic/class/" + classId + "/mode",
                message
        );

        log.info("수업 모드 변경: classId={}, mode={}", classId, message);
    }
}