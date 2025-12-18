package com.sooscode.sooscode_api.infra.websocket.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket 세션 Redis 저장소
 * - 순수 CRUD만 담당
 *
 * Redis 키 구조:
 * - ws:session:{sessionId}      → Hash (userId, username, classId, isInstructor)
 * - ws:user:{userId}:session    → String (sessionId) - 중복 접속 체크용
 * - ws:class:{classId}:members  → Set (userId 목록)
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class SessionRedisStore {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String SESSION_PREFIX = "ws:session:";
    private static final String USER_SESSION_PREFIX = "ws:user:";
    private static final String USER_SESSION_SUFFIX = ":session";
    private static final String CLASS_MEMBERS_PREFIX = "ws:class:";
    private static final String CLASS_MEMBERS_SUFFIX = ":members";
    private static final long TTL_HOURS = 24;

    // ==================== 세션 CRUD ====================

    public void saveSession(String sessionId, Long userId, String username, boolean isInstructor) {
        String key = SESSION_PREFIX + sessionId;

        redisTemplate.opsForHash().put(key, "userId", userId);
        redisTemplate.opsForHash().put(key, "username", username != null ? username : "User#" + userId);
        redisTemplate.opsForHash().put(key, "isInstructor", isInstructor);
        redisTemplate.expire(key, TTL_HOURS, TimeUnit.HOURS);

        // 유저 → 세션 역매핑
        String userKey = USER_SESSION_PREFIX + userId + USER_SESSION_SUFFIX;
        redisTemplate.opsForValue().set(userKey, sessionId, TTL_HOURS, TimeUnit.HOURS);

        log.debug("세션 저장: sessionId={}, userId={}, username={}", sessionId, userId, username);
    }

    public void deleteSession(String sessionId) {
        Long userId = getUserId(sessionId);

        // 세션 삭제
        redisTemplate.delete(SESSION_PREFIX + sessionId);

        // 유저 → 세션 매핑 삭제
        if (userId != null) {
            redisTemplate.delete(USER_SESSION_PREFIX + userId + USER_SESSION_SUFFIX);
        }

        log.debug("세션 삭제: sessionId={}", sessionId);
    }

    public Long getUserId(String sessionId) {
        Object value = redisTemplate.opsForHash().get(SESSION_PREFIX + sessionId, "userId");
        return value != null ? ((Number) value).longValue() : null;
    }

    public String getUsername(String sessionId) {
        Object value = redisTemplate.opsForHash().get(SESSION_PREFIX + sessionId, "username");
        return value != null ? value.toString() : null;
    }

    public String getClassId(String sessionId) {
        Object value = redisTemplate.opsForHash().get(SESSION_PREFIX + sessionId, "classId");
        return value != null ? value.toString() : null;
    }

    public boolean isInstructor(String sessionId) {
        Object value = redisTemplate.opsForHash().get(SESSION_PREFIX + sessionId, "isInstructor");
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        return Boolean.parseBoolean(value.toString());
    }

    public String getSessionIdByUserId(Long userId) {
        Object value = redisTemplate.opsForValue().get(USER_SESSION_PREFIX + userId + USER_SESSION_SUFFIX);
        return value != null ? value.toString() : null;
    }

    // ==================== 클래스 입장/퇴장 ====================

    public void setClassId(String sessionId, String classId) {
        redisTemplate.opsForHash().put(SESSION_PREFIX + sessionId, "classId", classId);
    }

    public void clearClassId(String sessionId) {
        redisTemplate.opsForHash().delete(SESSION_PREFIX + sessionId, "classId");
    }

    public void addClassMember(String classId, Long userId) {
        String key = CLASS_MEMBERS_PREFIX + classId + CLASS_MEMBERS_SUFFIX;
        redisTemplate.opsForSet().add(key, userId);
        redisTemplate.expire(key, TTL_HOURS, TimeUnit.HOURS);
    }

    public void removeClassMember(String classId, Long userId) {
        String key = CLASS_MEMBERS_PREFIX + classId + CLASS_MEMBERS_SUFFIX;
        redisTemplate.opsForSet().remove(key, userId);
    }

    public Set<Object> getClassMembers(String classId) {
        String key = CLASS_MEMBERS_PREFIX + classId + CLASS_MEMBERS_SUFFIX;
        Set<Object> members = redisTemplate.opsForSet().members(key);
        return members != null ? members : Collections.emptySet();
    }

    public void clearClassMembers(String classId) {
        redisTemplate.delete(CLASS_MEMBERS_PREFIX + classId + CLASS_MEMBERS_SUFFIX);
    }
}