package com.sooscode.sooscode_api.application.chat.controller;

import com.sooscode.sooscode_api.application.chat.dto.ChatHistoryResponse;
import com.sooscode.sooscode_api.application.chat.dto.ChatReactionRequest;
import com.sooscode.sooscode_api.application.chat.dto.ChatSaveRequest;
import com.sooscode.sooscode_api.application.chat.service.ChatMessageReactionService;
import com.sooscode.sooscode_api.application.chat.service.ChatService;
import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessageReaction;
import com.sooscode.sooscode_api.global.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = { "http://localhost:5173", "http://10.41.0.89:5173" })
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatMessageController {
    private final ChatService chatService;
    private final ChatMessageReactionService chatMessageReactionService;

    @GetMapping("/history")
    public List<ChatHistoryResponse> findAllByClassRoom_ClassIdOrderByCreatedAtAsc(@RequestParam("classId") Long classId) {
        return chatService.getHistoryByClassRoom_ClassIdOrderByCreatedAtAsc(classId);
    }
    @MessageMapping("/chat.send")
    public void send(ChatSaveRequest chatSaveRequest){
        chatService.saveAndBroadcast(chatSaveRequest);
    }
    @PostMapping("/chat.react")
    public int countReaction(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody ChatReactionRequest chatReactionRequest){
        Long userId = customUserDetails.getUser().getUserId();
        Long chatId = chatReactionRequest.getChatId();
        return chatMessageReactionService.addorRemoveReaction(userId, chatId);

    }


}
