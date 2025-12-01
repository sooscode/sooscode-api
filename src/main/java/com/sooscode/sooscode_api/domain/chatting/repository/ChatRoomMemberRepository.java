package com.sooscode.sooscode_api.domain.chatting.repository;

import com.sooscode.sooscode_api.domain.chatting.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, String > {
    boolean existsByNickname(String nickname);
    List<ChatRoomMember> findByNickname(String nickname);
}
