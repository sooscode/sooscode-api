package com.sooscode.sooscode_api.application.chat.service;

import com.sooscode.sooscode_api.application.chat.dto.ChatMessageRequest;
import com.sooscode.sooscode_api.application.chat.dto.ChatMessageResponse;
import com.sooscode.sooscode_api.application.chat.dto.EnterOrExitResponse;
import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessage;
import com.sooscode.sooscode_api.domain.chatmessage.repository.ChatMessageRepository;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.ChatStatus;
import com.sooscode.sooscode_api.global.api.status.ClassStatus;
import com.sooscode.sooscode_api.global.api.status.UserStatus;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Builder
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;

    @Override
    public ChatMessageResponse saveMessage(ChatMessageRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserStatus.NOT_FOUND));

        ClassRoom classRoom = classRoomRepository.findById(request.getClassId())
                .orElseThrow(() -> new CustomException(ClassStatus.CLASS_NOT_FOUND));

        ChatMessage entity = ChatMessage.builder()
                .user(user)
                .classRoom(classRoom)
                .content(request.getContent())
                .build();

        ChatMessage saved = chatMessageRepository.save(entity);

        return ChatMessageResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> findAllByClassRoom(Long classId) {
        List<ChatMessage> messages =
                chatMessageRepository.findAllByClassRoom_ClassIdOrderByCreatedAtAsc(classId);

        return messages.stream()
                .map(ChatMessageResponse::from)
                .toList();
    }
    @Transactional
    @Override
    public void deleteMessage(Long classId, Long chatId, Long userId){
        ChatMessage chatMessage = chatMessageRepository.findById(chatId)
                .orElseThrow(() -> new CustomException(ChatStatus.NOT_FOUND));
        
        // 자신것만 삭제가능 유효성검사
        if(!chatMessage.getUser().getUserId().equals(userId)){
            throw new CustomException(ChatStatus.ACCESS_DENIED);
        }
        if(chatMessage.isDeleted()){
            return;
        }
        chatMessage.markDeleted();
    }
    @Override
    public EnterOrExitResponse enterchatRoom(Long userId, Long classRoomId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserStatus.NOT_FOUND));

        return new EnterOrExitResponse(
                user.getEmail(),
                user.getName()
        );
    }
    @Override
    public EnterOrExitResponse exitchatRoom(Long userId, Long classRoomId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserStatus.NOT_FOUND));

        return new EnterOrExitResponse(
                user.getEmail(),
                user.getName()
        );
    }
}
