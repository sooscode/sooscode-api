package com.sooscode.sooscode_api.application.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 DTO
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private Long chatId;
    private Long classId;
    private Long userId;
    private String username;
    private String content;
    private MessageType type;
    private LocalDateTime createdAt;

    // 답장 관련
    private Long replyToChatId;
    private String replyToUsername;
    private String replyToContent;

    // 상태
    private boolean deleted;
    private int reactionCount;

    public enum MessageType {
        CHAT,       // 일반 메시지
        SYSTEM,     // 시스템 메시지 (입장/퇴장)
        DELETE,     // 삭제됨
        REACTION    // 리액션 업데이트
    }

    /**
     * 삭제 처리
     */
    public ChatMessageDto markDeleted() {
        return this.toBuilder()
                .deleted(true)
                .content("삭제된 메시지입니다.")
                .build();
    }

    /**
     * 시스템 메시지 생성
     */
    public static ChatMessageDto system(Long classId, String content) {
        return ChatMessageDto.builder()
                .classId(classId)
                .content(content)
                .type(MessageType.SYSTEM)
                .createdAt(LocalDateTime.now())
                .build();
    }
}