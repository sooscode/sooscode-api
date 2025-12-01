package com.sooscode.sooscode_api.domain.chatting.service;

import java.util.List;

public interface ChatMessageReactionService {

    // 좋아요 누르기
    void like(Long messageId, String reactorNickname);

    // 좋아요 개수
    long countLikes(Long messageId);

    // 좋아요 누른 사람들 닉네임 리스트
    List<String> getReactors(Long messageId);


}
