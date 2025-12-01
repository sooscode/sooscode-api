package com.sooscode.sooscode_api.domain.chatting.repository;

import com.sooscode.sooscode_api.domain.chatting.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 기존 전체 조회
    List<ChatMessage> findAllByOrderByIdAsc();

    // 🔥 방 별로 조회
    List<ChatMessage> findAllByRoomOrderByIdAsc(String room);
}
