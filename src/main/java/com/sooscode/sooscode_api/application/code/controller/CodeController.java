package com.sooscode.sooscode_api.application.code.controller;

import com.sooscode.sooscode_api.application.code.dto.CodeShareDto;
import com.sooscode.sooscode_api.application.code.service.AutoSaveService;
import com.sooscode.sooscode_api.global.websocket.WebSocketSessionRegistry;
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
public class CodeController {

    private final WebSocketSessionRegistry sessionRegistry;
    private final AutoSaveService autoSaveService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 강사 코드 공유
     * - 강사가 /app/code/instructor/{classId}로 전송
     * - /topic/code/instructor/{classId}로 브로드캐스트 (학생들이 구독)
     */
    @MessageMapping("/code/instructor/{classId}")
    public void shareInstructorCode(
            @DestinationVariable Long classId,
            CodeShareDto dto,
            StompHeaderAccessor accessor
    ) {
        String sessionId = accessor.getSessionId();
        Long userId = sessionRegistry.getUserId(sessionId);

        if (userId == null) {
            return;
        }

        // 강사 여부 확인
        boolean isInstructor = sessionRegistry.isInstructor(sessionId);
        if (!isInstructor) {
            return;
        }

        dto.setClassId(classId);
        dto.setUserId(userId);
        dto.setInstructor(true);

        // 자동 저장
        autoSaveService.autoSave(dto);

        // 학생들에게 브로드캐스트
        String topic = "/topic/code/instructor/" + classId;
        messagingTemplate.convertAndSend(topic, dto);

//        log.info("강사 코드 브로드캐스트 → {}, userId={}, codeLength={}",
//                topic, userId, dto.getCode().length());
    }

    /**
     * 학생 코드 공유
     * - 학생이 /app/code/student/{classId}로 전송
     * - /topic/code/student/{classId}로 브로드캐스트 (강사가 구독)
     */
    @MessageMapping("/code/student/{classId}")
    public void shareStudentCode(
            @DestinationVariable Long classId,
            CodeShareDto dto,
            StompHeaderAccessor accessor
    ) {
        String sessionId = accessor.getSessionId();
        Long userId = sessionRegistry.getUserId(sessionId);

        if (userId == null) {
            return;
        }

        // 학생 여부 확인
        boolean isInstructor = sessionRegistry.isInstructor(sessionId);
        if (isInstructor) {
            return;
        }

        dto.setClassId(classId);
        dto.setUserId(userId);
        dto.setInstructor(false);

        // 자동 저장
        autoSaveService.autoSave(dto);

        // 강사에게 브로드캐스트
        String topic = "/topic/code/student/" + classId;
        messagingTemplate.convertAndSend(topic, dto);

//        log.info("학생 코드 브로드캐스트 → {}, userId={}, codeLength={}",
//                topic, userId, dto.getCode().length());
    }
}