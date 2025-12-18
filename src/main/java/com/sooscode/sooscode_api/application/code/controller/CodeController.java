package com.sooscode.sooscode_api.application.code.controller;

import com.sooscode.sooscode_api.application.code.service.CodeService;
import com.sooscode.sooscode_api.infra.websocket.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * 코드 공유 WebSocket 컨트롤러
 *
 * 강사 → 학생:
 * - 전송: /app/code/instructor/{classId}
 * - 구독: /topic/code/instructor/{classId}
 *
 * 학생 → 강사:
 * - 전송: /app/code/student/{classId}
 * - 구독: /topic/code/student/{classId}/{studentId}
 *
 * 강사 → 학생 코드 수정:
 * - 전송: /app/code/instructor/{classId}/edit/{studentId}
 * - 구독: /topic/code/student/{classId}/{studentId}
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class CodeController {

    private final SessionService sessionService;
    private final CodeService codeService;

    /**
     * 강사 코드 공유
     */
    @MessageMapping("/code/instructor/{classId}")
    public void shareInstructorCode(
            @DestinationVariable Long classId,
            @Payload CodeRequest request,
            StompHeaderAccessor accessor
    ) {
        String sessionId = accessor.getSessionId();
        log.info("[CodeController] 강사 코드 공유 요청 - classId={}, sessionId={}", classId, sessionId);
        log.info("[CodeController] 요청 데이터 - code.length={}, language={}",
                request.code() != null ? request.code().length() : 0,
                request.language());

        Long userId = sessionService.getUserId(sessionId);

        if (userId == null) {
            log.warn("[CodeController] 인증되지 않은 사용자 - sessionId={}", sessionId);
            return;
        }

        if (!sessionService.isInstructor(sessionId)) {
            log.warn("[CodeController] 강사 권한 없음 - userId={}, sessionId={}", userId, sessionId);
            return;
        }

        String username = sessionService.getUsername(sessionId);
        log.info("[CodeController] 강사 코드 공유 실행 - classId={}, userId={}, username={}",
                classId, userId, username);

        codeService.shareInstructorCode(classId, userId, username, request.code(), request.language());

        log.info("[CodeController] 강사 코드 공유 완료 - classId={}, userId={}", classId, userId);
    }

    /**
     * 학생 코드 공유
     */
    @MessageMapping("/code/student/{classId}")
    public void shareStudentCode(
            @DestinationVariable Long classId,
            @Payload CodeRequest request,
            StompHeaderAccessor accessor
    ) {
        String sessionId = accessor.getSessionId();
        log.info("[CodeController] 학생 코드 공유 요청 - classId={}, sessionId={}", classId, sessionId);
        log.info("[CodeController] 요청 데이터 - code.length={}, language={}",
                request.code() != null ? request.code().length() : 0,
                request.language());

        Long userId = sessionService.getUserId(sessionId);

        if (userId == null) {
            log.warn("[CodeController] 인증되지 않은 사용자 - sessionId={}", sessionId);
            return;
        }

        if (sessionService.isInstructor(sessionId)) {
            log.warn("[CodeController] 학생만 사용 가능 - userId={}, sessionId={}", userId, sessionId);
            return;
        }

        String username = sessionService.getUsername(sessionId);
        log.info("[CodeController] 학생 코드 공유 실행 - classId={}, userId={}, username={}",
                classId, userId, username);

        codeService.shareStudentCode(classId, userId, username, request.code(), request.language());

        log.info("[CodeController] 학생 코드 공유 완료 - classId={}, userId={}", classId, userId);
    }

    /**
     * 강사가 학생 코드 수정
     * - 강사만 호출 가능
     * - 해당 학생의 토픽으로 브로드캐스트되어 학생 에디터에 반영됨
     */
    @MessageMapping("/code/instructor/{classId}/edit/{studentId}")
    public void editStudentCode(
            @DestinationVariable Long classId,
            @DestinationVariable Long studentId,
            @Payload CodeRequest request,
            StompHeaderAccessor accessor
    ) {
        String sessionId = accessor.getSessionId();
        log.info("[CodeController] 강사→학생 코드 수정 요청 - classId={}, studentId={}, sessionId={}",
                classId, studentId, sessionId);
        log.info("[CodeController] 요청 데이터 - code.length={}, language={}",
                request.code() != null ? request.code().length() : 0,
                request.language());

        Long userId = sessionService.getUserId(sessionId);

        if (userId == null) {
            log.warn("[CodeController] 인증되지 않은 사용자 - sessionId={}", sessionId);
            return;
        }

        if (!sessionService.isInstructor(sessionId)) {
            log.warn("[CodeController] 강사 권한 없음 - userId={}, sessionId={}", userId, sessionId);
            return;
        }

        String username = sessionService.getUsername(sessionId);
        log.info("[CodeController] 강사→학생 코드 수정 실행 - classId={}, studentId={}, instructorId={}, instructorName={}",
                classId, studentId, userId, username);

        codeService.editStudentCodeByInstructor(
                classId,
                studentId,
                userId,
                username,
                request.code(),
                request.language()
        );

        log.info("[CodeController] 강사→학생 코드 수정 완료 - classId={}, studentId={}, instructorId={}",
                classId, studentId, userId);
    }

    /**
     * 코드 전송 요청 DTO
     */
    public record CodeRequest(String code, String language) {}
}