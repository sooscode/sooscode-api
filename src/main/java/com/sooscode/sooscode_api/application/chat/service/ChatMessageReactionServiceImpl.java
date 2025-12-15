package com.sooscode.sooscode_api.application.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sooscode.sooscode_api.application.chat.dto.ChatMessageResponse;
import com.sooscode.sooscode_api.application.chat.dto.ChatMessageType;
import com.sooscode.sooscode_api.application.chat.dto.ChatReactionMessage;
import com.sooscode.sooscode_api.application.chat.dto.ChatReactionUserResponse;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.ChatStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatMessageReactionServiceImpl implements ChatMessageReactionService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper; // chatKey에서 classId 뽑을 때
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final  UserRepository userRepository;

    @Override
    @Transactional
    public int addorRemoveReaction(Long userId, Long chatId) {

        //  메시지 존재 확인 + classId 추출 (Redis에서)
        Object raw = redisTemplate.opsForValue().get(chatKey(chatId));
        if (raw == null) throw new CustomException(ChatStatus.NOT_FOUND);

        // 네가 Redis에 뭘 저장하냐에 따라:
        // - ChatMessageResponse를 저장 중이면 ChatMessageResponse로 convert
        // - Redis 전용 DTO(ChatMessageRedisDto)로 바꿨으면 그걸로 convert
        ChatMessageResponse msg = objectMapper.convertValue(raw, ChatMessageResponse.class);

        Long classId = msg.getClassId();
        if (classId == null) throw new CustomException(ChatStatus.ACCESS_DENIED);

        //  삭제된 메시지면 리액션 막기(정책)
        if (msg.isDeleted()) throw new CustomException(ChatStatus.ACCESS_DENIED);

        String reactionKey = reactionKey(chatId);
        String member = String.valueOf(userId);

        Boolean already = redisTemplate.opsForSet().isMember(reactionKey, member);

        if (Boolean.TRUE.equals(already)) {
            redisTemplate.opsForSet().remove(reactionKey, member);
        } else {
            redisTemplate.opsForSet().add(reactionKey, member);
        }

        Long countLong = redisTemplate.opsForSet().size(reactionKey);
        int count = countLong == null ? 0 : countLong.intValue();

        //  브로드캐스트 DTO (너 기존거 그대로)
        ChatReactionMessage broadcast = new ChatReactionMessage(
                chatId,
                count,
                classId,
                ChatMessageType.REACTION
        );

        simpMessagingTemplate.convertAndSend(
                "/topic/class/" + classId + "/chat",
                broadcast
        );

        return count;
    }
    public List<ChatReactionUserResponse> getReactionUsers(Long chatId){
        Set<Object> members = redisTemplate.opsForSet().members(reactionKey(chatId));
        if (members == null || members.isEmpty()) return List.of();

        List<Long> userIds = members.stream()
                .map(String::valueOf)
                .map(Long::valueOf)
                .toList();

        return userRepository.findAllById(userIds).stream()
                .map(u -> new ChatReactionUserResponse(u.getUserId(), u.getName()))
                .toList();
    }
    public boolean reactedByMe(Long userId, Long chatId){
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(reactionKey(chatId), String.valueOf(userId))
        );
    }

    private String chatKey(Long chatId) {
        return "ws:chat:" + chatId;
    }

    private String reactionKey(Long chatId) {
        return "ws:chat:" + chatId + ":reactions";
    }

}