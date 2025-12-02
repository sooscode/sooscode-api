package com.sooscode.sooscode_api.application.chat.controller;

import com.sooscode.sooscode_api.application.chat.dto.ChatHistoryResponse;
import com.sooscode.sooscode_api.application.chat.dto.ChatSaveRequest;
import com.sooscode.sooscode_api.application.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = { "http://localhost:5173", "http://10.41.0.89:5173" })
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatMessageController {
    private final ChatService chatService;

    @GetMapping("/history")
    public List<ChatHistoryResponse> findAllByClassRoom_ClassIdOrderByCreatedAtAsc(@RequestParam("classId") Long classId) {
        return chatService.getHistoryByClassRoom_ClassIdOrderByCreatedAtAsc(classId);
    }
    @MessageMapping("/chat.send")
    public void send(ChatSaveRequest chatSaveRequest){
        chatService.saveAndBroadcast(chatSaveRequest);
    }

}
