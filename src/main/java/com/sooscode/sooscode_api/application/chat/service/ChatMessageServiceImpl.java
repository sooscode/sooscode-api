package com.sooscode.sooscode_api.application.chat.service;

import com.sooscode.sooscode_api.application.chat.dto.ChatMessageRequest;
import com.sooscode.sooscode_api.application.chat.dto.ChatMessageResponse;
import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessage;
import com.sooscode.sooscode_api.domain.chatmessage.repository.ChatMessageRepository;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.errorcode.ClassErrorCode;
import com.sooscode.sooscode_api.global.exception.errorcode.UserErrorCode;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        ClassRoom classRoom = classRoomRepository.findById(request.getClassId())
                .orElseThrow(() -> new CustomException(ClassErrorCode.NOT_FOUND));

        ChatMessage entity = ChatMessage.builder()
                .user(user)
                .classRoom(classRoom)
                .content(request.getContent())
                .build();

        ChatMessage saved = chatMessageRepository.save(entity);

        return ChatMessageResponse.from(saved);
    }

    @Override
    public List<ChatMessageResponse> findAllByClassRoom(Long classId) {
        List<ChatMessage> messages =
                chatMessageRepository.findAllByClassRoom_ClassIdOrderByCreatedAtAsc(classId);

        return messages.stream()
                .map(ChatMessageResponse::from)
                .toList();
    }
}
