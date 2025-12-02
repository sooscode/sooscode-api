package com.sooscode.sooscode_api.domain.chatmessage.service;

import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessage;
import com.sooscode.sooscode_api.domain.chatmessage.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    @Override
    public ChatMessage saveMessage(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessage> findAllByClassRoom_ClassIdOrderByCreatedAtAsc(Long classId) {
        return chatMessageRepository.findAllByClassRoom_ClassIdOrderByCreatedAtAsc(classId);
    }
}
