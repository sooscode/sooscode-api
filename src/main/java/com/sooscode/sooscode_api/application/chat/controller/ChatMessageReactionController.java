package com.sooscode.sooscode_api.application.chat.controller;

import com.sooscode.sooscode_api.application.chat.dto.ChatReactionRequest;
import com.sooscode.sooscode_api.application.chat.dto.ChatReactionResponse;
import com.sooscode.sooscode_api.application.chat.dto.ChatReactionUserResponse;
import com.sooscode.sooscode_api.application.chat.service.ChatMessageReactionService;
import com.sooscode.sooscode_api.global.api.response.ApiResponse;
import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/{chatId}/reactionlist")
    public ResponseEntity<ApiResponse<List<ChatReactionUserResponse>>> reactionList(
            @PathVariable Long chatId
    ){
        List<ChatReactionUserResponse> list = chatMessageReactionService.getReactionUsers(chatId);
        return ApiResponse.ok(list);

    }
    @GetMapping("/{chatId}/reacted")
    public ResponseEntity<ApiResponse<Boolean>> reacted(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long chatId
    ) {
        Long userId = customUserDetails.getUser().getUserId();
        return ApiResponse.ok(chatMessageReactionService.reactedByMe(userId, chatId));
    }
}
