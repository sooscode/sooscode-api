package com.sooscode.sooscode_api.application.chat.controller;

import com.sooscode.sooscode_api.application.chat.dto.ChatReactionRequest;
import com.sooscode.sooscode_api.application.chat.dto.ChatReactionResponse;
import com.sooscode.sooscode_api.application.chat.service.ChatMessageReactionService;
import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatMessageReactionController {

    private final ChatMessageReactionService chatMessageReactionService;

    @PostMapping("/chat.react")
    public ChatReactionResponse countReaction(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                             @RequestBody ChatReactionRequest chatReactionRequest){
        Long userId = customUserDetails.getUser().getUserId();
        Long chatId = chatReactionRequest.getChatId();
        int count = chatMessageReactionService.addorRemoveReaction(userId, chatId);
        return new ChatReactionResponse(count);

    }
}
