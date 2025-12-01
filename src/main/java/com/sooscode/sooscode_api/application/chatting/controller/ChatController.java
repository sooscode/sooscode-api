package com.sooscode.sooscode_api.application.chatting.controller;

import com.sooscode.sooscode_api.domain.chatting.entity.ChatMessage;
import com.sooscode.sooscode_api.domain.chatting.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;



@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;


    @MessageMapping("/chat.send")
    public void send(ChatMessage message) {
        chatService.saveAndBroadcast(message);
    }
}
