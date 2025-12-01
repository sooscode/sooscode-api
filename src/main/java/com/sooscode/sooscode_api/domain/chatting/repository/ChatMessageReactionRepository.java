package com.sooscode.sooscode_api.domain.chatting.repository;

import com.sooscode.sooscode_api.domain.chatting.entity.ChatMessageReaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageReactionRepository
        extends JpaRepository<ChatMessageReaction, Long> {

    boolean existsByMessageIdAndReactor(Long messageId, String reactor);

    long countByMessageId(Long messageId);

    List<ChatMessageReaction> findByMessageId(Long messageId);
}
