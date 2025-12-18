package com.sooscode.sooscode_api.application.classroom.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sooscode.sooscode_api.application.classroom.dto.ParticipantInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 참여자 정보 Redis 저장소
 *
 * Redis 키 구조:
 * - participant:class:{classId} → Hash (userId → ParticipantInfo JSON)
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class ParticipantRedisStore {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "participant:class:";
    private static final long TTL_HOURS = 24;

    private String getKey(String classId) {
        return KEY_PREFIX + classId;
    }

    public void save(String classId, Long userId, String username, boolean isInstructor) {
        ParticipantInfo info = ParticipantInfo.builder()
                .userId(userId)
                .username(username)
                .instructor(isInstructor)
                .joinedAt(LocalDateTime.now())
                .build();

        try {
            String json = objectMapper.writeValueAsString(info);
            redisTemplate.opsForHash().put(getKey(classId), userId.toString(), json);
            redisTemplate.expire(getKey(classId), TTL_HOURS, TimeUnit.HOURS);
            log.debug("참여자 저장: classId={}, userId={}, username={}", classId, userId, username);
        } catch (JsonProcessingException e) {
            log.error("참여자 저장 실패", e);
        }
    }

    public void delete(String classId, Long userId) {
        redisTemplate.opsForHash().delete(getKey(classId), userId.toString());
        log.debug("참여자 삭제: classId={}, userId={}", classId, userId);
    }

    public ParticipantInfo findOne(String classId, Long userId) {
        Object value = redisTemplate.opsForHash().get(getKey(classId), userId.toString());
        return parseParticipant(value);
    }

    public List<ParticipantInfo> findAll(String classId) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(getKey(classId));

        if (entries.isEmpty()) {
            return Collections.emptyList();
        }

        return entries.values().stream()
                .map(this::parseParticipant)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<ParticipantInfo> findStudents(String classId) {
        return findAll(classId).stream()
                .filter(p -> !p.isInstructor())
                .collect(Collectors.toList());
    }

    public Optional<ParticipantInfo> findInstructor(String classId) {
        return findAll(classId).stream()
                .filter(ParticipantInfo::isInstructor)
                .findFirst();
    }

    public long count(String classId) {
        Long size = redisTemplate.opsForHash().size(getKey(classId));
        return size != null ? size : 0;
    }

    public boolean exists(String classId, Long userId) {
        return redisTemplate.opsForHash().hasKey(getKey(classId), userId.toString());
    }

    public void deleteAll(String classId) {
        redisTemplate.delete(getKey(classId));
        log.debug("클래스 참여자 전체 삭제: classId={}", classId);
    }

    private ParticipantInfo parseParticipant(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.readValue(value.toString(), ParticipantInfo.class);
        } catch (JsonProcessingException e) {
            log.error("참여자 파싱 실패", e);
            return null;
        }
    }
}