package com.sooscode.sooscode_api.application.chat.service;

import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.status.ChatStatus;
import com.sooscode.sooscode_api.application.chat.dto.ChatDto;
import com.sooscode.sooscode_api.application.chat.dto.ChatMessageDto;
import com.sooscode.sooscode_api.application.chat.store.ChatRedisStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 채팅 서비스
 * - 메시지 저장/조회/삭제
 * - 리액션 처리
 * - 브로드캐스트
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatRedisStore chatRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String CHAT_TOPIC = "/topic/class/%d/chat";
    private static final String TYPING_TOPIC = "/topic/class/%d/typing";
    private static final int MAX_CONTENT_LENGTH = 500;

    // ==================== 메시지 ====================

    /**
     * 채팅 메시지 전송
     */
    public ChatMessageDto sendMessage(Long classId, Long userId, String username, ChatDto.SendRequest request) {
        // 내용 검증
        String content = request.getContent();
        if (content == null || content.isBlank()) {
            throw new CustomException(ChatStatus.NOT_EMPTY);
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new CustomException(ChatStatus.CONTENT_TOO_LONG);
        }

        // 답장 처리
        String replyToUsername = null;
        String replyToContent = null;

        if (request.getReplyToChatId() != null) {
            ChatMessageDto replyTo = chatRepository.findById(request.getReplyToChatId());
            if (replyTo == null) {
                throw new CustomException(ChatStatus.NOT_FOUND);
            }
            if (!replyTo.getClassId().equals(classId)) {
                throw new CustomException(ChatStatus.ACCESS_DENIED);
            }
            replyToUsername = replyTo.getUsername();
            replyToContent = replyTo.getContent();
        }

        // 메시지 생성
        Long chatId = chatRepository.nextChatId();
        ChatMessageDto message = ChatMessageDto.builder()
                .chatId(chatId)
                .classId(classId)
                .userId(userId)
                .username(username)
                .content(content)
                .type(ChatMessageDto.MessageType.CHAT)
                .createdAt(LocalDateTime.now())
                .replyToChatId(request.getReplyToChatId())
                .replyToUsername(replyToUsername)
                .replyToContent(replyToContent)
                .deleted(false)
                .reactionCount(0)
                .build();

        // 저장
        chatRepository.saveMessage(message);

        // 브로드캐스트
        broadcast(classId, message);

        log.info("채팅 전송: classId={}, chatId={}, userId={}", classId, chatId, userId);
        return message;
    }

    /**
     * 채팅 메시지 삭제 (소프트 삭제)
     */
    public ChatDto.DeleteResponse deleteMessage(Long classId, Long chatId, Long userId) {
        ChatMessageDto message = chatRepository.findById(chatId);

        if (message == null) {
            throw new CustomException(ChatStatus.NOT_FOUND);
        }
        if (!message.getClassId().equals(classId)) {
            throw new CustomException(ChatStatus.ACCESS_DENIED);
        }
        if (!message.getUserId().equals(userId)) {
            throw new CustomException(ChatStatus.ACCESS_DENIED);
        }
        if (message.isDeleted()) {
            throw new CustomException(ChatStatus.ACCESS_DENIED);
        }

        // 소프트 삭제
        ChatMessageDto deleted = message.markDeleted();
        chatRepository.updateMessage(deleted);

        // 삭제 알림 브로드캐스트
        ChatDto.DeleteResponse response = ChatDto.DeleteResponse.builder()
                .chatId(chatId)
                .classId(classId)
                .type(ChatMessageDto.MessageType.DELETE)
                .build();

        broadcast(classId, response);

        log.info("채팅 삭제: classId={}, chatId={}, userId={}", classId, chatId, userId);
        return response;
    }

    /**
     * 채팅 히스토리 조회
     */
    public ChatDto.HistoryResponse getHistory(Long classId) {
        List<ChatMessageDto> messages = chatRepository.findAllByClassId(classId);

        return ChatDto.HistoryResponse.builder()
                .classId(classId)
                .messages(messages)
                .totalCount(messages.size())
                .build();
    }

    // ==================== 리액션 ====================

    /**
     * 리액션 토글
     */
    public ChatDto.ReactionResponse toggleReaction(Long classId, Long chatId, Long userId) {
        ChatMessageDto message = chatRepository.findById(chatId);

        if (message == null) {
            throw new CustomException(ChatStatus.NOT_FOUND);
        }
        if (!message.getClassId().equals(classId)) {
            throw new CustomException(ChatStatus.ACCESS_DENIED);
        }
        if (message.isDeleted()) {
            throw new CustomException(ChatStatus.ACCESS_DENIED);
        }

        // 리액션 토글
        int count = chatRepository.toggleReaction(chatId, userId);

        // 브로드캐스트
        ChatDto.ReactionResponse response = ChatDto.ReactionResponse.builder()
                .chatId(chatId)
                .classId(classId)
                .count(count)
                .type(ChatMessageDto.MessageType.REACTION)
                .build();

        broadcast(classId, response);

        log.debug("리액션 토글: chatId={}, userId={}, count={}", chatId, userId, count);
        return response;
    }

    /**
     * 리액션한 유저 목록
     */
    public List<ChatDto.ReactionUser> getReactionUsers(Long chatId) {
        Set<Long> userIds = chatRepository.getReactionUserIds(chatId);

        if (userIds.isEmpty()) {
            return List.of();
        }

        return userRepository.findAllById(userIds).stream()
                .map(user -> ChatDto.ReactionUser.builder()
                        .userId(user.getUserId())
                        .username(user.getName())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 내가 리액션했는지 확인
     */
    public boolean hasReacted(Long chatId, Long userId) {
        return chatRepository.hasReacted(chatId, userId);
    }

    // ==================== 타이핑 ====================

    /**
     * 타이핑 상태 브로드캐스트
     */
    public void broadcastTyping(Long classId, Long userId, String username, boolean typing) {
        ChatDto.TypingResponse response = ChatDto.TypingResponse.builder()
                .classId(classId)
                .userId(userId)
                .username(username)
                .typing(typing)
                .build();

        String topic = String.format(TYPING_TOPIC, classId);
        messagingTemplate.convertAndSend(topic, response);

        log.debug("타이핑 상태: classId={}, userId={}, typing={}", classId, userId, typing);
    }

    // ==================== 정리 ====================

    /**
     * 클래스 채팅 전체 삭제 (수업 종료 시)
     */
    public void cleanup(Long classId) {
        chatRepository.deleteAllByClassId(classId);
        log.info("채팅 데이터 정리: classId={}", classId);
    }

    // ==================== 내부 메서드 ====================

    private void broadcast(Long classId, Object message) {
        String topic = String.format(CHAT_TOPIC, classId);
        messagingTemplate.convertAndSend(topic, message);
    }
}