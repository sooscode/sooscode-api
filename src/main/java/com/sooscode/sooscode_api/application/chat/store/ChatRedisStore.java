package com.sooscode.sooscode_api.application.chat.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sooscode.sooscode_api.application.chat.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 채팅 메시지 Redis 저장소
 *
 * Redis 키 구조:
 * - chat:seq                          → 채팅 ID 시퀀스 (auto increment)
 * - chat:message:{chatId}             → 채팅 메시지 (JSON)
 * - chat:class:{classId}:messages     → 클래스별 채팅 ID 목록 (ZSet, score=timestamp)
 * - chat:message:{chatId}:reactions   → 리액션한 userId 목록 (Set)
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class ChatRedisStore {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String SEQ_KEY = "chat:seq";
    private static final String MESSAGE_KEY = "chat:message:%d";
    private static final String CLASS_MESSAGES_KEY = "chat:class:%d:messages";
    private static final String REACTION_KEY = "chat:message:%d:reactions";
    private static final long TTL_HOURS = 24;

    // ==================== 메시지 CRUD ====================

    /**
     * 새 채팅 ID 발급
     */
    public Long nextChatId() {
        Long id = redisTemplate.opsForValue().increment(SEQ_KEY);
        return id != null ? id : 1L;
    }

    /**
     * 메시지 저장
     */
    public void saveMessage(ChatMessageDto message) {
        String key = String.format(MESSAGE_KEY, message.getChatId());
        try {
            String json = objectMapper.writeValueAsString(message);
            redisTemplate.opsForValue().set(key, json, TTL_HOURS, TimeUnit.HOURS);

            // 클래스별 메시지 인덱스에 추가 (시간순 정렬)
            String indexKey = String.format(CLASS_MESSAGES_KEY, message.getClassId());
            double score = System.currentTimeMillis();
            redisTemplate.opsForZSet().add(indexKey, message.getChatId().toString(), score);
            redisTemplate.expire(indexKey, TTL_HOURS, TimeUnit.HOURS);

            log.debug("채팅 저장: chatId={}, classId={}", message.getChatId(), message.getClassId());
        } catch (JsonProcessingException e) {
            log.error("채팅 저장 실패", e);
        }
    }

    /**
     * 메시지 조회
     */
    public ChatMessageDto findById(Long chatId) {
        String key = String.format(MESSAGE_KEY, chatId);
        return parseMessage(redisTemplate.opsForValue().get(key));
    }

    /**
     * 메시지 업데이트 (삭제 처리 등)
     */
    public void updateMessage(ChatMessageDto message) {
        String key = String.format(MESSAGE_KEY, message.getChatId());
        try {
            String json = objectMapper.writeValueAsString(message);
            redisTemplate.opsForValue().set(key, json, TTL_HOURS, TimeUnit.HOURS);
        } catch (JsonProcessingException e) {
            log.error("채팅 업데이트 실패", e);
        }
    }

    /**
     * 클래스별 전체 메시지 조회 (시간순)
     */
    public List<ChatMessageDto> findAllByClassId(Long classId) {
        String indexKey = String.format(CLASS_MESSAGES_KEY, classId);

        // 1. 채팅 ID 목록 조회 (시간순)
        Set<Object> chatIdsRaw = redisTemplate.opsForZSet().range(indexKey, 0, -1);
        if (chatIdsRaw == null || chatIdsRaw.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> chatIds = chatIdsRaw.stream()
                .map(Object::toString)
                .map(Long::valueOf)
                .collect(Collectors.toList());

        // 2. 메시지 키 목록 생성
        List<String> keys = chatIds.stream()
                .map(id -> String.format(MESSAGE_KEY, id))
                .collect(Collectors.toList());

        // 3. 한 번에 조회 (multiGet)
        List<Object> raws = redisTemplate.opsForValue().multiGet(keys);
        if (raws == null) {
            return Collections.emptyList();
        }

        // 4. 메시지 파싱
        List<ChatMessageDto> messages = raws.stream()
                .filter(Objects::nonNull)
                .map(this::parseMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 5. 리액션 카운트 조회 (pipeline)
        List<Object> counts = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long chatId : chatIds) {
                byte[] key = redisTemplate.getStringSerializer()
                        .serialize(String.format(REACTION_KEY, chatId));
                if (key != null) {
                    connection.setCommands().sCard(key);
                }
            }
            return null;
        });

        // 6. 리액션 카운트 주입
        List<ChatMessageDto> result = new ArrayList<>(messages.size());
        for (int i = 0; i < messages.size(); i++) {
            int reactionCount = 0;
            if (i < counts.size() && counts.get(i) != null) {
                reactionCount = ((Number) counts.get(i)).intValue();
            }
            result.add(messages.get(i).toBuilder()
                    .reactionCount(reactionCount)
                    .build());
        }

        return result;
    }

    // ==================== 리액션 ====================

    /**
     * 리액션 토글 (추가/제거)
     * @return 현재 리액션 수
     */
    public int toggleReaction(Long chatId, Long userId) {
        String key = String.format(REACTION_KEY, chatId);
        String member = userId.toString();

        Boolean exists = redisTemplate.opsForSet().isMember(key, member);

        if (Boolean.TRUE.equals(exists)) {
            redisTemplate.opsForSet().remove(key, member);
        } else {
            redisTemplate.opsForSet().add(key, member);
            redisTemplate.expire(key, TTL_HOURS, TimeUnit.HOURS);
        }

        Long count = redisTemplate.opsForSet().size(key);
        return count != null ? count.intValue() : 0;
    }

    /**
     * 리액션 수 조회
     */
    public int getReactionCount(Long chatId) {
        String key = String.format(REACTION_KEY, chatId);
        Long count = redisTemplate.opsForSet().size(key);
        return count != null ? count.intValue() : 0;
    }

    /**
     * 리액션한 유저 ID 목록
     */
    public Set<Long> getReactionUserIds(Long chatId) {
        String key = String.format(REACTION_KEY, chatId);
        Set<Object> members = redisTemplate.opsForSet().members(key);

        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }

        return members.stream()
                .map(Object::toString)
                .map(Long::valueOf)
                .collect(Collectors.toSet());
    }

    /**
     * 내가 리액션했는지 확인
     */
    public boolean hasReacted(Long chatId, Long userId) {
        String key = String.format(REACTION_KEY, chatId);
        Boolean result = redisTemplate.opsForSet().isMember(key, userId.toString());
        return Boolean.TRUE.equals(result);
    }

    // ==================== 정리 ====================

    /**
     * 클래스 채팅 전체 삭제 (수업 종료 시)
     */
    public void deleteAllByClassId(Long classId) {
        String indexKey = String.format(CLASS_MESSAGES_KEY, classId);

        // 채팅 ID 목록 조회
        Set<Object> chatIds = redisTemplate.opsForZSet().range(indexKey, 0, -1);

        if (chatIds != null && !chatIds.isEmpty()) {
            // 메시지 및 리액션 삭제
            for (Object chatIdObj : chatIds) {
                Long chatId = Long.valueOf(chatIdObj.toString());
                redisTemplate.delete(String.format(MESSAGE_KEY, chatId));
                redisTemplate.delete(String.format(REACTION_KEY, chatId));
            }
        }

        // 인덱스 삭제
        redisTemplate.delete(indexKey);

        log.info("클래스 채팅 전체 삭제: classId={}", classId);
    }

    // ==================== 내부 메서드 ====================

    private ChatMessageDto parseMessage(Object value) {
        if (value == null) return null;
        try {
            if (value instanceof String) {
                return objectMapper.readValue((String) value, ChatMessageDto.class);
            }
            return objectMapper.convertValue(value, ChatMessageDto.class);
        } catch (Exception e) {
            log.error("채팅 파싱 실패", e);
            return null;
        }
    }
}