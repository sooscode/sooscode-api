package com.sooscode.sooscode_api.global.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket 세션 정보를 Redis에 저장/조회하는 저장소
 * - 순수 CRUD 작업만 담당
 * - 비즈니스 로직은 ClassSocketService에서 처리
 *
 * Redis 키 구조:
 * - ws:session:{sessionId}      → Hash (userId, classId, isInstructor)
 * - ws:user:{userId}:session    → String (sessionId) - 중복 접속 체크용
 * - ws:class:{classId}:members  → Set (userId 목록)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketSessionRegistry {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_KEY_PREFIX = "ws:session:";
    private static final String CLASS_MEMBERS_KEY_PREFIX = "ws:class:";
    private static final String CLASS_MEMBERS_KEY_SUFFIX = ":members";
    private static final String USER_SESSION_KEY_PREFIX = "ws:user:";
    private static final String USER_SESSION_KEY_SUFFIX = ":session";
    private static final long SESSION_TTL_HOURS = 24;


    // ==================== 세션 관리 ====================

    /**
     * 새 세션 등록
     */
    public void registerSession(String sessionId, Long userId) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        redisTemplate.opsForHash().put(sessionKey, "userId", userId);
        redisTemplate.expire(sessionKey, SESSION_TTL_HOURS, TimeUnit.HOURS);

        // 유저 → 세션 매핑 (중복 접속 체크용)
        String userSessionKey = USER_SESSION_KEY_PREFIX + userId + USER_SESSION_KEY_SUFFIX;
        redisTemplate.opsForValue().set(userSessionKey, sessionId);

        log.debug("Session registered: sessionId={}, userId={}", sessionId, userId);
    }

    /**
     * 새 세션 등록 (역할 포함) 오버로딩
     */
    public void registerSession(String sessionId, Long userId, boolean isInstructor) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        redisTemplate.opsForHash().put(sessionKey, "userId", userId);
        redisTemplate.opsForHash().put(sessionKey, "isInstructor", isInstructor);
        redisTemplate.expire(sessionKey, SESSION_TTL_HOURS, TimeUnit.HOURS);

        // 유저 → 세션 매핑 (중복 접속 체크용)
        String userSessionKey = USER_SESSION_KEY_PREFIX + userId + USER_SESSION_KEY_SUFFIX;
        redisTemplate.opsForValue().set(userSessionKey, sessionId);

        log.debug("Session registered: sessionId={}, userId={}, isInstructor={}",
                sessionId, userId, isInstructor);
    }



    /**
     * 세션 삭제
     */
    public void removeSession(String sessionId) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        redisTemplate.delete(sessionKey);
        log.debug("Session removed: sessionId={}", sessionId);
    }

    /**
     * 세션에서 userId 조회
     */
    public Long getUserId(String sessionId) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        Object value = redisTemplate.opsForHash().get(sessionKey, "userId");
        return value != null ? ((Number) value).longValue() : null;
    }

    /**
     * 세션에서 classId 조회
     */
    public String getClassId(String sessionId) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        Object value = redisTemplate.opsForHash().get(sessionKey, "classId");
        return value != null ? value.toString() : null;
    }

    /**
     * 강사 여부 확인
     */
    public boolean isInstructor(String sessionId) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        Object value = redisTemplate.opsForHash().get(sessionKey, "isInstructor");
        if (value == null) {
            return false;
        }
        // Boolean 또는 String으로 저장될 수 있으므로 두 경우 모두 처리
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(value.toString());
    }

    /**
     * 역할 설정 (나중에 설정하는 경우)
     */
    public void setRole(String sessionId, boolean isInstructor) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(sessionKey))) {
            redisTemplate.opsForHash().put(sessionKey, "isInstructor", isInstructor);
            log.debug("Role updated: sessionId={}, isInstructor={}", sessionId, isInstructor);
        }
    }



    // ==================== 중복 접속 관리 ====================

    /**
     * 유저의 기존 세션 ID 조회 (중복 접속 체크용)
     */
    public String getExistingSessionId(Long userId) {
        String userSessionKey = USER_SESSION_KEY_PREFIX + userId + USER_SESSION_KEY_SUFFIX;
        Object value = redisTemplate.opsForValue().get(userSessionKey);
        return value != null ? value.toString() : null;
    }

    /**
     * 유저 → 세션 매핑 삭제
     */
    public void removeUserSessionMapping(Long userId) {
        String userSessionKey = USER_SESSION_KEY_PREFIX + userId + USER_SESSION_KEY_SUFFIX;
        redisTemplate.delete(userSessionKey);
    }


    // ==================== 클래스 입장/퇴장 ====================

    /**
     * 클래스 입장 처리
     */
    public void joinClass(String sessionId, String classId, Long userId) {
        // 세션에 classId 추가
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        redisTemplate.opsForHash().put(sessionKey, "classId", classId);

        // 클래스 멤버 목록에 추가
        String classKey = CLASS_MEMBERS_KEY_PREFIX + classId + CLASS_MEMBERS_KEY_SUFFIX;
        redisTemplate.opsForSet().add(classKey, userId);

        log.debug("Class joined: classId={}, userId={}, sessionId={}", classId, userId, sessionId);
    }

    /**
     * 클래스 퇴장 처리
     */
    public void leaveClass(String classId, Long userId) {
        String classKey = CLASS_MEMBERS_KEY_PREFIX + classId + CLASS_MEMBERS_KEY_SUFFIX;
        redisTemplate.opsForSet().remove(classKey, userId);
        log.debug("Class left: classId={}, userId={}", classId, userId);
    }

    /**
     * 세션에서 classId 제거 (클래스 퇴장 시)
     */
    public void clearClassFromSession(String sessionId) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        redisTemplate.opsForHash().delete(sessionKey, "classId");
    }


    // ==================== 조회 ====================

    /**
     * 클래스에 현재 접속 중인 멤버 목록 조회
     */
    public Set<Object> getClassMembers(String classId) {
        String classKey = CLASS_MEMBERS_KEY_PREFIX + classId + CLASS_MEMBERS_KEY_SUFFIX;
        Set<Object> members = redisTemplate.opsForSet().members(classKey);
        return members != null ? members : Collections.emptySet();
    }

    /**
     * 클래스 멤버 전체 삭제 (수업 종료 시)
     */
    public void clearClassMembers(String classId) {
        String classKey = CLASS_MEMBERS_KEY_PREFIX + classId + CLASS_MEMBERS_KEY_SUFFIX;
        redisTemplate.delete(classKey);
        log.debug("Class members cleared: classId={}", classId);
    }
}

