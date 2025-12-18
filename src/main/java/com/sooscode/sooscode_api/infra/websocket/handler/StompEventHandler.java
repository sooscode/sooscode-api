package com.sooscode.sooscode_api.infra.websocket.handler;

import com.sooscode.sooscode_api.infra.security.CustomUserDetails;
import com.sooscode.sooscode_api.application.classroom.service.ParticipantService;
import com.sooscode.sooscode_api.infra.websocket.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

/**
 * WebSocket 이벤트 리스너
 * - SessionConnectEvent: 연결 시 세션 등록
 * - SessionSubscribeEvent: 구독 시 클래스 입장
 * - SessionDisconnectEvent: 연결 해제 시 정리
 *
 * ChannelInterceptor 대신 EventListener 사용으로 순환 의존성 해결
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class StompEventHandler {

    private final SessionService sessionService;
    private final ParticipantService participantService;

    /**
     * WebSocket 연결 시
     */
    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (!(accessor.getUser() instanceof UsernamePasswordAuthenticationToken auth)) {
            return;
        }
        if (!(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
            return;
        }

        String sessionId = accessor.getSessionId();
        Long userId = userDetails.getUser().getUserId();
        String username = userDetails.getUser().getName();
        boolean isInstructor = extractIsInstructor(accessor);

        // 중복 접속 처리
        String oldSessionId = sessionService.getExistingSessionId(userId);
        if (oldSessionId != null && !oldSessionId.equals(sessionId)) {
            handleDuplicateSession(oldSessionId, userId);
        }

        // 세션 등록
        sessionService.register(sessionId, userId, username, isInstructor);

        log.info("WS CONNECT — sessionId={}, userId={}, username={}, isInstructor={}",
                sessionId, userId, username, isInstructor);
    }

    /**
     * 구독 시 (채팅 채널 구독 = 클래스 입장)
     */
    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();

        // 채팅 채널 구독만 입장으로 처리
        if (!isChatChannel(destination)) {
            return;
        }

        Long userId = sessionService.getUserId(sessionId);
        String classId = extractClassId(destination);

        if (userId == null || classId == null) {
            return;
        }

        // 이미 입장한 경우 무시
        String currentClassId = sessionService.getClassId(sessionId);
        if (classId.equals(currentClassId)) {
            log.debug("이미 입장한 클래스: classId={}, userId={}", classId, userId);
            return;
        }

        String username = sessionService.getUsername(sessionId);
        boolean isInstructor = sessionService.isInstructor(sessionId);

        // 세션에 클래스 기록
        sessionService.joinClass(sessionId, classId, userId);

        // 참여자 목록에 추가 + 브로드캐스트
        participantService.join(classId, userId, username, isInstructor);

        log.info("CLASS JOIN — classId={}, userId={}, username={}, isInstructor={}",
                classId, userId, username, isInstructor);
    }

    /**
     * WebSocket 연결 해제 시
     */
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        Long userId = sessionService.getUserId(sessionId);
        String classId = sessionService.getClassId(sessionId);

        // 클래스 퇴장 처리
        if (classId != null && userId != null) {
            sessionService.leaveClass(sessionId, classId, userId);
            participantService.leave(classId, userId);
            log.info("CLASS LEAVE — classId={}, userId={}", classId, userId);
        }

        // 세션 삭제
        sessionService.remove(sessionId);

        log.info("WS DISCONNECT — sessionId={}, userId={}", sessionId, userId);
    }

    /**
     * 중복 접속 세션 정리
     */
    private void handleDuplicateSession(String oldSessionId, Long userId) {
        String oldClassId = sessionService.getClassId(oldSessionId);

        if (oldClassId != null) {
            sessionService.leaveClass(oldSessionId, oldClassId, userId);
            participantService.leave(oldClassId, userId);
            log.info("FORCE CLASS LEAVE — classId={}, userId={}", oldClassId, userId);
        }

        sessionService.remove(oldSessionId);
        log.info("FORCE DISCONNECT — oldSessionId={}, userId={}", oldSessionId, userId);
    }

    /**
     * 헤더에서 강사 여부 추출
     */
    private boolean extractIsInstructor(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader("isInstructor");
        if (header != null) {
            return Boolean.parseBoolean(header);
        }
        log.warn("역할 정보 없음, 기본값(학생) 사용");
        return false;
    }

    /**
     * 채팅 채널 여부 확인
     */
    private boolean isChatChannel(String destination) {
        return destination != null
                && destination.startsWith("/topic/class/")
                && destination.endsWith("/chat");
    }

    /**
     * destination에서 classId 추출
     */
    private String extractClassId(String destination) {
        // /topic/class/{classId}/chat
        String[] parts = destination.split("/");
        return parts.length >= 4 ? parts[3] : null;
    }
}