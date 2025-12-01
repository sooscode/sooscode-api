package com.sooscode.sooscode_api.domain.chatting.service;

import com.sooscode.sooscode_api.domain.chatting.entity.ChatMessage;

import java.util.List;

public interface ChatService {

    void saveAndBroadcast(ChatMessage message);


    List<ChatMessage> getHistoryByRoom(String room);

}
