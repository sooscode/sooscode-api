package com.sooscode.sooscode_api.application.chat.controller;

import com.sooscode.sooscode_api.application.chat.dto.TypingRequest;
import com.sooscode.sooscode_api.application.chat.dto.TypingResponse;
import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class ChatTypingController{

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.typing")
    public void typing(TypingRequest req, Principal principal) {

        CustomUserDetails user = (CustomUserDetails) ((Authentication) principal).getPrincipal();

        TypingResponse res = new TypingResponse(
                req.getClassId(),
                user.getUser().getUserId(),
                user.getUser().getEmail(),
                user.getUser().getName(),
                true
        );
        messagingTemplate.convertAndSend("/topic/chat/" + req.getClassId() + "/typing", res);
    }
    @MessageMapping("/chat.stopTyping")
    public void stopTyping(TypingRequest req, Principal principal) {

        CustomUserDetails user = (CustomUserDetails) ((Authentication) principal).getPrincipal();

        TypingResponse res = new TypingResponse(
                req.getClassId(),
                user.getUser().getUserId(),
                user.getUser().getEmail(),
                user.getUser().getName(),
                false
        );

        messagingTemplate.convertAndSend("/topic/chat/" + req.getClassId() + "/typing", res);
    }
}

