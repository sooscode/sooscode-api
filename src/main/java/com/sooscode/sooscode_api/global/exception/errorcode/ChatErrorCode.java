package com.sooscode.sooscode_api.global.exception.errorcode;

import com.sooscode.sooscode_api.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_001", "채팅을 찾을 수 없습니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHAT_002", "채팅 접근 권한이 없습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
