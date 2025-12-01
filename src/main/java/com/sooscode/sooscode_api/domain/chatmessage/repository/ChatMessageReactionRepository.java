package com.sooscode.sooscode_api.domain.chatmessage.repository;

import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessageReaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageReactionRepository extends JpaRepository<ChatMessageReaction, Long> {

    // ✅ 특정 메시지(chatId) + 특정 유저(reactor)의 좋아요 여부 확인
    boolean existsByMessage_ChatIdAndReactor(Long chatId, String reactor);

    // ✅ 특정 메시지(chatId)의 좋아요 개수
    long countByMessage_ChatId(Long chatId);

    // ✅ 특정 메시지(chatId)의 모든 리액션 목록
    List<ChatMessageReaction> findByMessage_ChatId(Long chatId);
}
