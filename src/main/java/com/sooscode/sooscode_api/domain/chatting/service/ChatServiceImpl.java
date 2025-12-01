package com.sooscode.sooscode_api.domain.chatting.service;

import com.sooscode.sooscode_api.domain.chatting.entity.ChatMessage;
import com.sooscode.sooscode_api.domain.chatting.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {
    private final ChatMessageRepository repository;
    private final SimpMessagingTemplate messagingTemplate;


    @Override
    public void saveAndBroadcast(ChatMessage message) {
        // room 없으면 기본값 1번방
        if (message.getRoom() == null || message.getRoom().isBlank()) {
            message.setRoom("1");
        }

        message.setCreatedAt(LocalDateTime.now());
        ChatMessage saved = repository.save(message);

        //  /topic/chat/{room} 으로 브로드캐스트
        String destination = "/topic/chat/" + saved.getRoom();
        messagingTemplate.convertAndSend(destination, saved);
    }

    @Override
    public List<ChatMessage> getHistoryByRoom(String room) {
        return repository.findAllByRoomOrderByIdAsc(room);
    }
}
