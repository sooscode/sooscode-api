package com.sooscode.sooscode_api.application.chat.service;

import com.sooscode.sooscode_api.application.chat.dto.ChatReactionMessage;
import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessage;
import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessageReaction;
import com.sooscode.sooscode_api.domain.chatmessage.repository.ChatMessageReactionRepository;
import com.sooscode.sooscode_api.domain.chatmessage.repository.ChatMessageRepository;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.ChatStatus;
import com.sooscode.sooscode_api.global.api.status.UserStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageReactionServiceImpl implements ChatMessageReactionService {

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageReactionRepository chatMessageReactionRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    @Transactional
    public int addorRemoveReaction(Long userId, Long chatId) {
        User user = userRepository.findById(userId).
                 orElseThrow(() -> new CustomException(UserStatus.NOT_FOUND));

        ChatMessage chatMessage = chatMessageRepository.findById(chatId).
                orElseThrow(() -> new CustomException(ChatStatus.NOT_FOUND));

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

        int count = chatMessageReactionRepository.countByMessage(chatMessage);

        // 🔥 브로드캐스트를 위해 classId 가져오기
        Long classId = chatMessage.getClassRoom().getClassId();

        // 🔥 모든 사용자에게 업데이트 내용 전송할 DTO
        ChatReactionMessage broadcast = new ChatReactionMessage(// type
                chatId,                  // 어떤 메시지인지
                count,                   // 현재 공감 총합
                classId                  // 어떤 class 채팅방인지
        );

        // 🔥 WebSocket 브로드캐스트
        simpMessagingTemplate.convertAndSend(
                "/topic/chat/" + classId,
                broadcast
        );

        // 컨트롤러 반환은 기존대로 count만
        return count;


    }
}
