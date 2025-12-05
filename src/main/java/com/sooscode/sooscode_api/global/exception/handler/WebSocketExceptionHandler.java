package com.sooscode.sooscode_api.global.exception.handler;

import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.ErrorResponse;
import com.sooscode.sooscode_api.global.exception.errorcode.GlobalErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;

@Slf4j
@Component
@ControllerAdvice
public class WebSocketExceptionHandler {

    /**
     * WebSocket에서 발생한 CustomException 처리
     */
    @MessageExceptionHandler(CustomException.class)
    @SendToUser("/queue/errors")  // 에러를 보낸 사용자에게만 전달
    public ErrorResponse handleCustomException(CustomException e, Principal principal) {
        String userName = principal != null ? principal.getName() : "Unknown";

        log.error("[WebSocket CustomException] 사용자: {}, 코드: {}, 메시지: {}",
                userName, e.getErrorCode().getCode(), e.getMessage());

        return ErrorResponse.of(e.getErrorCode());
    }

    /**
     * WebSocket에서 발생한 일반 예외 처리
     */
    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleException(Exception e, Principal principal) {
        String userName = principal != null ? principal.getName() : "Unknown";

        log.error("[WebSocket Exception] 사용자: {}, 에러: {}", userName, e.getMessage(), e);

        return ErrorResponse.of(GlobalErrorCode.INTERNAL_SERVER_ERROR);
    }
}