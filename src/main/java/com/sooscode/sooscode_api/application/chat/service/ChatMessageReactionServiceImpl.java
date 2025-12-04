package com.sooscode.sooscode_api.application.chat.service;

import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessage;
import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessageReaction;
import com.sooscode.sooscode_api.domain.chatmessage.repository.ChatMessageReactionRepository;
import com.sooscode.sooscode_api.domain.chatmessage.repository.ChatMessageRepository;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageReactionServiceImpl implements ChatMessageReactionService {

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageReactionRepository chatMessageReactionRepository;

    @Override
    public int addorRemoveReaction(Long userId, Long chatId) {
        User user = userRepository.findById(userId).
                 orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        ChatMessage chatMessage = chatMessageRepository.findById(chatId).
                orElseThrow(() -> new IllegalArgumentException("채팅 없음"));

        boolean alreadyexist = chatMessageReactionRepository.existsByMessageAndUser(chatMessage, user);

        if(alreadyexist){
            chatMessageReactionRepository.deleteByMessageAndUser(chatMessage, user);
        }else{
            ChatMessageReaction chatMessageReaction = new ChatMessageReaction();
            chatMessageReaction.setUser(user); // 컬럼 추가되면서 countById로
            chatMessageReaction.setMessage(chatMessage);
            chatMessageReaction.setCreatedAt(LocalDateTime.now());

            chatMessageReactionRepository.save(chatMessageReaction);
        }

        return chatMessageReactionRepository.countByMessage(chatMessage);


    }
}
