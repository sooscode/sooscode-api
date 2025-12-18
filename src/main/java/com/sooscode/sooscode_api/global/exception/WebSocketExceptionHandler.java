package com.sooscode.sooscode_api.global.exception;

import com.sooscode.sooscode_api.global.response.ApiResponse;
import com.sooscode.sooscode_api.global.status.GlobalStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;

/**
 * WebSocket 예외 처리 핸들러
 */
@Slf4j
@Component
@ControllerAdvice
public class WebSocketExceptionHandler {

    /**
     * WebSocket에서 발생한 CustomException 처리
     */
    @MessageExceptionHandler(CustomException.class)
    @SendToUser("/queue/errors")
    public ApiResponse<Void> handleCustomException(CustomException e, Principal principal) {
        String userName = principal != null ? principal.getName() : "Unknown";

        log.error("[WebSocket CustomException] 사용자: {}, 코드: {}, 메시지: {}",
                userName, e.getStatusCode().getCode(), e.getMessage());

        return ApiResponse.failBody(e.getStatusCode());
    }

    /**
     * WebSocket에서 발생한 일반 예외 처리
     */
    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ApiResponse<Void> handleException(Exception e, Principal principal) {
        String userName = principal != null ? principal.getName() : "Unknown";

        log.error("[WebSocket Exception] 사용자: {}, 에러: {}", userName, e.getMessage(), e);

        return ApiResponse.failBody(GlobalStatus.INTERNAL_SERVER_ERROR);
    }
}