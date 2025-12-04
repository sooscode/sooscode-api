package com.sooscode.sooscode_api.domain.chatmessage.repository;

import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessage;
import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessageReaction;
import com.sooscode.sooscode_api.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageReactionRepository extends JpaRepository<ChatMessageReaction, Long> {

    // 이미 공감했는지 여부
    boolean existsByMessageAndUser(ChatMessage message, User user);

    // 공감 취소
    void deleteByMessageAndUser(ChatMessage message, User user);

    // 해당 메시지의 공감 총 개수
    int countByMessage(ChatMessage message);
}