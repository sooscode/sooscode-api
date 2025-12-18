package com.sooscode.sooscode_api.application.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 채팅 관련 Request/Response DTO 모음
 */
public class ChatDto {

    // ==================== 채팅 메시지 ====================

    /**
     * 채팅 전송 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendRequest {
        private String content;
        private Long replyToChatId;  // 답장 대상 (선택)
    }

    /**
     * 채팅 삭제 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteRequest {
        private Long chatId;
    }

    /**
     * 채팅 삭제 응답 (브로드캐스트용)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteResponse {
        private Long chatId;
        private Long classId;
        private ChatMessageDto.MessageType type;
    }

    // ==================== 리액션 ====================

    /**
     * 리액션 토글 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReactionRequest {
        private Long chatId;
    }

    /**
     * 리액션 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReactionResponse {
        private Long chatId;
        private Long classId;
        private int count;
        private ChatMessageDto.MessageType type;
    }

    /**
     * 리액션한 유저 정보
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReactionUser {
        private Long userId;
        private String username;
    }

    // ==================== 타이핑 ====================

    /**
     * 타이핑 상태 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypingRequest {
        private Long classId;
        private boolean typing;  // true: 타이핑 중, false: 타이핑 중지
    }

    /**
     * 타이핑 상태 응답 (브로드캐스트용)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypingResponse {
        private Long classId;
        private Long userId;
        private String username;
        private boolean typing;
    }

    // ==================== 채팅 히스토리 ====================

    /**
     * 채팅 히스토리 응답
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryResponse {
        private Long classId;
        private List<ChatMessageDto> messages;
        private int totalCount;
    }
}