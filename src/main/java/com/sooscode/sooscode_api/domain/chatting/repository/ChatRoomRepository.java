package com.sooscode.sooscode_api.domain.chatting.repository;

import com.sooscode.sooscode_api.domain.chatting.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
