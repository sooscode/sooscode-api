package com.sooscode.sooscode_api.application.chat.controller;

import com.sooscode.sooscode_api.application.classroom.service.ParticipantService;
import com.sooscode.sooscode_api.application.code.dto.CodeData;
import com.sooscode.sooscode_api.application.code.dto.CodeResponse;
import com.sooscode.sooscode_api.application.code.service.CodeService;
import com.sooscode.sooscode_api.global.response.ApiResponse;
import com.sooscode.sooscode_api.global.status.ChatStatus;
import com.sooscode.sooscode_api.infra.security.CustomUserDetails;
import com.sooscode.sooscode_api.application.chat.dto.ChatDto;
import com.sooscode.sooscode_api.application.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅 REST API
 * - 채팅 히스토리 조회
 * - 리액션 관련 조회
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    // ==================== 채팅 히스토리 ====================

    /**
     * 채팅 히스토리 조회
     * GET /api/chat/history?classId={classId}
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<ChatDto.HistoryResponse>> getHistory(
            @RequestParam Long classId
    ) {
        ChatDto.HistoryResponse response = chatService.getHistory(classId);
        return ApiResponse.ok(ChatStatus.READ_OK, response);
    }

    // ==================== 리액션 ====================

    /**
     * 리액션 토글 (REST 방식)
     * POST /api/chat/reaction
     */
    @PostMapping("/reaction")
    public ResponseEntity<ApiResponse<ChatDto.ReactionResponse>> toggleReaction(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ChatDto.ReactionRequest request
    ) {
        Long userId = userDetails.getUser().getUserId();

        // classId를 찾기 위해 메시지 조회 필요 - 서비스에서 처리
        // 여기서는 classId를 request에 포함시키거나, 서비스에서 찾도록 수정 필요
        // 일단 간단하게 처리
        ChatDto.ReactionResponse response = chatService.toggleReaction(
                null, // classId는 서비스에서 메시지로부터 추출
                request.getChatId(),
                userId
        );

        return ApiResponse.ok(ChatStatus.OK, response);
    }

    /**
     * 리액션한 유저 목록
     * GET /api/chat/{chatId}/reactions
     */
    @GetMapping("/{chatId}/reactions")
    public ResponseEntity<ApiResponse<List<ChatDto.ReactionUser>>> getReactionUsers(
            @PathVariable Long chatId
    ) {
        List<ChatDto.ReactionUser> users = chatService.getReactionUsers(chatId);
        return ApiResponse.ok(ChatStatus.OK, users);
    }

    /**
     * 내가 리액션했는지 확인
     * GET /api/chat/{chatId}/reacted
     */
    @GetMapping("/{chatId}/reacted")
    public ResponseEntity<ApiResponse<Boolean>> hasReacted(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long chatId
    ) {
        Long userId = userDetails.getUser().getUserId();
        boolean reacted = chatService.hasReacted(chatId, userId);
        return ApiResponse.ok(ChatStatus.OK, reacted);
    }
}