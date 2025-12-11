package com.sooscode.sooscode_api.application.chat.controller;

import com.sooscode.sooscode_api.application.chat.dto.ChatMessageResponse;
import com.sooscode.sooscode_api.application.chat.dto.ChatMessageRequest;
import com.sooscode.sooscode_api.application.chat.dto.ChatMessageType;
import com.sooscode.sooscode_api.application.chat.dto.EnterOrExitResponse;
import com.sooscode.sooscode_api.application.chat.service.ChatMessageService;
import com.sooscode.sooscode_api.domain.chatmessage.entity.ChatMessage;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.response.ApiResponse;
import com.sooscode.sooscode_api.global.api.status.ChatStatus;
import com.sooscode.sooscode_api.global.api.status.ClassStatus;
import com.sooscode.sooscode_api.global.api.status.SnapshotStatus;
import com.sooscode.sooscode_api.global.api.status.UserStatus;
import com.sooscode.sooscode_api.global.websocket.WebSocketSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@CrossOrigin(origins = { "http://localhost:5173/chat", "http://10.41.0.89:5173" })
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatMessageController {

    private final WebSocketSessionRegistry sessionRegistry;
    private final ChatMessageService chatMessageService;


    /**
     * 코드 공유 메시지 처리
     * 클라이언트 → SEND /app/code/{classId}
     * 서버 → /topic/code/{classId} 로 브로드캐스트
     */
    @MessageMapping("/chat/{classId}")
    @SendTo("/topic/chat/{classId}")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> chatMessage(
            @DestinationVariable Long classId,
            ChatMessageRequest request,
            StompHeaderAccessor accessor
    ) {
        String sessionId = accessor.getSessionId();
        Long userId = sessionRegistry.getUserId(sessionId);

        userEffectiveness(sessionId, userId);// 유저 Id 유효성 검사

        classEffectiveness(classId); // 클래스 Id 유효성검사

        request.setClassId(classId);
        ChatMessageResponse response = chatMessageService.saveMessage(request, userId);

        return ApiResponse.ok(ChatStatus.OK,response);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> findAllByClassRoom_ClassIdOrderByCreatedAtAsc(@RequestParam("classId") Long classId) {
        classEffectiveness(classId); // 클래스 Id 유효성검사
        List<ChatMessageResponse> response = chatMessageService.findAllByClassRoom(classId);
        return  ApiResponse.ok(ChatStatus.READ_OK,response);
    }
    @MessageMapping("/chat/{classId}/enter")
    @SendTo("/topic/chat/{classId}")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> enterChat(
            @DestinationVariable Long classId,
            StompHeaderAccessor accessor
    ) {
        String sessionId = accessor.getSessionId();
        Long userId = sessionRegistry.getUserId(sessionId);

        userEffectiveness(sessionId, userId);// 유저 Id 유효성 검사

        classEffectiveness(classId); // 클래스 Id 유효성검사

        EnterOrExitResponse response = chatMessageService.enterchatRoom(userId, classId);
        ChatMessageResponse enter = ChatMessageResponse.system(
                classId,
                userId,
                response.getEmail(),
                response.getName(),
                response.getName() + "님이 입장하셨습니다.",
                ChatMessageType.ENTER

        );

        return ApiResponse.ok(ChatStatus.ENTER_OK, enter);
    }
    @MessageMapping("/chat/{classId}/exit")
    @SendTo("/topic/chat/{classId}")
    public ResponseEntity<ApiResponse<ChatMessageResponse>> exitChat(
            @DestinationVariable Long classId,
            StompHeaderAccessor accessor
    ) {
        String sessionId = accessor.getSessionId();
        Long userId = sessionRegistry.getUserId(sessionId);

        userEffectiveness(sessionId, userId);// 유저 Id 유효성 검사

        classEffectiveness(classId); // 클래스 Id 유효성검사

        EnterOrExitResponse response = chatMessageService.exitchatRoom(userId, classId);
        ChatMessageResponse exit = ChatMessageResponse.system(
                classId,
                userId,
                response.getEmail(),
                response.getName(),
                response.getName() + "님이 퇴장하셨습니다.",
                ChatMessageType.EXIT
        );

        return ApiResponse.ok(ChatStatus.EXIT_OK, exit);
    }
    private void classEffectiveness(Long classId){
        if (classId == null) {
            throw new CustomException(ClassStatus.CLASS_NOT_FOUND);
        }
    }

    private void userEffectiveness(String sessionId, Long userId){
        if (sessionId == null || userId == null) {
            throw new CustomException(UserStatus.NOT_FOUND);
        }
    }
}