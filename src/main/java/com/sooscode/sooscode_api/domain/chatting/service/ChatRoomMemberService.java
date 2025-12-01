package com.sooscode.sooscode_api.domain.chatting.service;

import com.sooscode.sooscode_api.application.chatting.dto.ChatRoomDto;

import java.util.List;

public interface ChatRoomMemberService {

    boolean existsByNickname(String nickname);

    List<ChatRoomDto> findByNickname(String nickname);

}
