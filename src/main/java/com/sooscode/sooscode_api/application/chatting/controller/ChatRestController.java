package com.sooscode.sooscode_api.application.chatting.controller;

import com.sooscode.sooscode_api.domain.chatting.entity.ChatMessage;
import com.sooscode.sooscode_api.domain.chatting.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = { "http://localhost:5173", "http://10.41.0.89:5173" })
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ChatService chatService;

    // 🔥 room 파라미터 받아서 그 방 히스토리만 리턴
    @GetMapping("/history")
    public List<ChatMessage> getHistory(@RequestParam("room") String room) {
        return chatService.getHistoryByRoom(room);
    }
}
