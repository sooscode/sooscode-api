package com.sooscode.sooscode_api.application.chat.service;


import com.sooscode.sooscode_api.application.chat.dto.ChatReactionUserResponse;

import java.util.List;

public interface ChatMessageReactionService {
    // 공감 추가 또는 삭제
    int addorRemoveReaction(Long uesrId, Long chatId);
    // 특정 메세지의 공감한 사람의 이름 리스트
    List<ChatReactionUserResponse> getReactionUsers(Long chatId);
    // 내가 reaction 눌렀는지 안눌렀는지 여부
    boolean reactedByMe(Long userId, Long chatId);


}
