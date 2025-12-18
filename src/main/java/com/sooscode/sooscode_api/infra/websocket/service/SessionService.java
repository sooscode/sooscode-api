package com.sooscode.sooscode_api.infra.websocket.service;

import com.sooscode.sooscode_api.infra.websocket.store.SessionRedisStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 세션 관리 서비스
 * - 세션 등록/삭제
 * - 클래스 입장/퇴장 (세션 레벨)
 * - 중복 접속 관리
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SessionService {

    private final SessionRedisStore sessionRepository;

    /**
     * 세션 등록
     */
    public void register(String sessionId, Long userId, String username, boolean isInstructor) {
        sessionRepository.saveSession(sessionId, userId, username, isInstructor);
        log.info("세션 등록: sessionId={}, userId={}, username={}, isInstructor={}",
                sessionId, userId, username, isInstructor);
    }

    /**
     * 세션 삭제
     */
    public void remove(String sessionId) {
        sessionRepository.deleteSession(sessionId);
        log.info("세션 삭제: sessionId={}", sessionId);
    }

    /**
     * 클래스 입장 (세션에 classId 기록)
     */
    public void joinClass(String sessionId, String classId, Long userId) {
        sessionRepository.setClassId(sessionId, classId);
        sessionRepository.addClassMember(classId, userId);
        log.debug("클래스 입장 (세션): sessionId={}, classId={}, userId={}", sessionId, classId, userId);
    }

    /**
     * 클래스 퇴장 (세션에서 classId 제거)
     */
    public void leaveClass(String sessionId, String classId, Long userId) {
        sessionRepository.clearClassId(sessionId);
        sessionRepository.removeClassMember(classId, userId);
        log.debug("클래스 퇴장 (세션): sessionId={}, classId={}, userId={}", sessionId, classId, userId);
    }

    /**
     * 중복 접속 확인 - 기존 세션 ID 반환
     */
    public String getExistingSessionId(Long userId) {
        return sessionRepository.getSessionIdByUserId(userId);
    }

    /**
     * 세션 정보 조회
     */
    public Long getUserId(String sessionId) {
        return sessionRepository.getUserId(sessionId);
    }

    public String getUsername(String sessionId) {
        return sessionRepository.getUsername(sessionId);
    }

    public String getClassId(String sessionId) {
        return sessionRepository.getClassId(sessionId);
    }

    public boolean isInstructor(String sessionId) {
        return sessionRepository.isInstructor(sessionId);
    }

    /**
     * 클래스 멤버 조회
     */
    public Set<Object> getClassMembers(String classId) {
        return sessionRepository.getClassMembers(classId);
    }

    /**
     * 클래스 멤버 전체 삭제
     */
    public void clearClassMembers(String classId) {
        sessionRepository.clearClassMembers(classId);
    }
}