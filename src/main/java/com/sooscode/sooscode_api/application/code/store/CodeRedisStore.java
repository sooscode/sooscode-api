package com.sooscode.sooscode_api.application.code.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sooscode.sooscode_api.application.code.dto.CodeData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 코드 데이터 Redis 저장소
 *
 * Redis 키 구조:
 * - code:class:{classId}:instructor           → 강사 코드 (JSON)
 * - code:class:{classId}:student:{userId}     → 학생별 코드 (JSON)
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class CodeRedisStore {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String INSTRUCTOR_KEY = "code:class:%d:instructor";
    private static final String STUDENT_KEY = "code:class:%d:student:%d";
    private static final long TTL_HOURS = 24;

    // ==================== 강사 코드 ====================

    public void saveInstructorCode(Long classId, CodeData data) {
        String key = String.format(INSTRUCTOR_KEY, classId);
        saveCode(key, data);
        log.debug("강사 코드 저장: classId={}", classId);
    }

    public CodeData getInstructorCode(Long classId) {
        String key = String.format(INSTRUCTOR_KEY, classId);
        return getCode(key);
    }

    public void deleteInstructorCode(Long classId) {
        redisTemplate.delete(String.format(INSTRUCTOR_KEY, classId));
    }

    // ==================== 학생 코드 ====================

    public void saveStudentCode(Long classId, Long userId, CodeData data) {
        String key = String.format(STUDENT_KEY, classId, userId);
        saveCode(key, data);
        log.debug("학생 코드 저장: classId={}, userId={}", classId, userId);
    }

    public CodeData getStudentCode(Long classId, Long userId) {
        String key = String.format(STUDENT_KEY, classId, userId);
        return getCode(key);
    }

    public void deleteStudentCode(Long classId, Long userId) {
        redisTemplate.delete(String.format(STUDENT_KEY, classId, userId));
    }

    public void deleteAllStudentCodes(Long classId) {
        String pattern = String.format("code:class:%d:student:*", classId);
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.debug("학생 코드 전체 삭제: classId={}, count={}", classId, keys.size());
        }
    }

    // ==================== 전체 삭제 ====================

    public void deleteAllClassCodes(Long classId) {
        deleteInstructorCode(classId);
        deleteAllStudentCodes(classId);
        log.info("클래스 코드 전체 삭제: classId={}", classId);
    }

    // ==================== 내부 메서드 ====================

    private void saveCode(String key, CodeData data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, json, TTL_HOURS, TimeUnit.HOURS);
        } catch (JsonProcessingException e) {
            log.error("코드 저장 실패: key={}", key, e);
        }
    }

    private CodeData getCode(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) return null;
            return objectMapper.readValue(value.toString(), CodeData.class);
        } catch (JsonProcessingException e) {
            log.error("코드 조회 실패: key={}", key, e);
            return null;
        }
    }
}