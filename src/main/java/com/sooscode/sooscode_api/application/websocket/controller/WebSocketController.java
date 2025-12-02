package com.sooscode.sooscode_api.application.websocket.controller;

import com.sooscode.sooscode_api.application.websocket.dto.ChatMessageDto;
import com.sooscode.sooscode_api.application.websocket.dto.CodeShareDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketController {

    // 채팅 메시지 처리
    @MessageMapping("/chat/{classId}")
    @SendTo("/topic/class/{classId}/chat")
    public ChatMessageDto sendChatMessage(
            @DestinationVariable Long classId,
            ChatMessageDto message
    ) {
        log.info("Chat message received - classId: {}, user: {}, content: {}",
                classId, message.getUserName(), message.getContent());

        message.setClassId(classId);
        message.setCreatedAt(LocalDateTime.now());

        return message;
    }

    // 코드 공유 처리
    @MessageMapping("/code/{classId}")
    @SendTo("/topic/class/{classId}/code")
    public CodeShareDto shareCode(
            @DestinationVariable Long classId,
            CodeShareDto codeShare
    ) {
        log.info("Code share received - classId: {}, user: {}, language: {}",
                classId, codeShare.getUserName(), codeShare.getLanguage());

        codeShare.setClassId(classId);

        return codeShare;
    }

    // 사용자 입장
    @MessageMapping("/join/{classId}")
    @SendTo("/topic/class/{classId}/chat")
    public ChatMessageDto userJoin(
            @DestinationVariable Long classId,
            ChatMessageDto message
    ) {
        message.setClassId(classId);
        message.setType(ChatMessageDto.MessageType.JOIN);
        message.setContent(message.getUserName() + " joined the class");
        message.setCreatedAt(LocalDateTime.now());

        return message;
    }

    // 사용자 퇴장
    @MessageMapping("/leave/{classId}")
    @SendTo("/topic/class/{classId}/chat")
    public ChatMessageDto userLeave(
            @DestinationVariable Long classId,
            ChatMessageDto message
    ) {
        message.setClassId(classId);
        message.setType(ChatMessageDto.MessageType.LEAVE);
        message.setContent(message.getUserName() + " left the class");
        message.setCreatedAt(LocalDateTime.now());

        return message;
    }
}