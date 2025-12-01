package com.sooscode.sooscode_api.domain.chatting.service;

import com.sooscode.sooscode_api.domain.chatting.entity.ChatMessage;
import com.sooscode.sooscode_api.domain.chatting.entity.ChatMessageReaction;
import com.sooscode.sooscode_api.domain.chatting.repository.ChatMessageReactionRepository;
import com.sooscode.sooscode_api.domain.chatting.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatMessageReactionServiceImpl implements ChatMessageReactionService {

    private final ChatMessageReactionRepository reactionRepository;
    private final ChatMessageRepository messageRepository;

    // 🔥 STOMP 브로드캐스트 용
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void like(Long messageId, String reactorNickname) {

        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageId));

        // 이미 이 닉네임으로 눌렀으면 무시
        if (reactionRepository.existsByMessageIdAndReactor(messageId, reactorNickname)) {
            return;
        }

        ChatMessageReaction reaction = new ChatMessageReaction();
        reaction.setMessage(message);
        reaction.setReactor(reactorNickname);
        reaction.setCreatedAt(LocalDateTime.now());

        reactionRepository.save(reaction);

        //  여기서 최신 좋아요 정보 계산
        long likeCount = reactionRepository.countByMessageId(messageId);
        List<String> reactors = reactionRepository.findByMessageId(messageId)
                .stream()
                .map(ChatMessageReaction::getReactor)
                .toList();

        //  STOMP로 보낼 payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("messageId", messageId);
        payload.put("likeCount", likeCount);
        payload.put("reactors", reactors);

        //  방 기준으로 브로드캐스트
        // 프론트에서 /topic/chat-reaction/{room} 구독하게 할 거야
        String room = message.getRoom(); // ChatMessage 에 room 필드 이미 있지
        messagingTemplate.convertAndSend(
                "/topic/chat-reaction/" + room,
                (Object) payload   //  Object로 강제 캐스팅
        );

    }

    @Override
    @Transactional(readOnly = true)
    public long countLikes(Long messageId) {
        return reactionRepository.countByMessageId(messageId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getReactors(Long messageId) {
        return reactionRepository.findByMessageId(messageId)
                .stream()
                .map(ChatMessageReaction::getReactor)
                .toList();
    }
}
