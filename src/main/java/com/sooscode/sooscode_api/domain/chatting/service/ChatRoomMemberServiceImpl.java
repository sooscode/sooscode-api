package com.sooscode.sooscode_api.domain.chatting.service;

import com.sooscode.sooscode_api.application.chatting.dto.ChatRoomDto;
import com.sooscode.sooscode_api.domain.chatting.entity.ChatRoom;
import com.sooscode.sooscode_api.domain.chatting.entity.ChatRoomMember;
import com.sooscode.sooscode_api.domain.chatting.repository.ChatRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomMemberServiceImpl implements ChatRoomMemberService {
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    @Override
    public boolean existsByNickname(String nickname) {
        return chatRoomMemberRepository.existsByNickname(nickname);
    }
    public List<ChatRoomDto> findByNickname(String nickname){

        List<ChatRoomMember> members =
                chatRoomMemberRepository.findByNickname(nickname);

        // 방 정보 DTO로 변환
        return members.stream()
                .map(member -> {
                    ChatRoom room = member.getChatRoom();
                    return new ChatRoomDto(room.getId());
                })
                .toList();
    }
}
