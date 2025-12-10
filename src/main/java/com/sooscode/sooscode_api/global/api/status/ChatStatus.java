package com.sooscode.sooscode_api.global.api.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatStatus implements StatusCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_001", "채팅을 찾을 수 없습니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHAT_002", "채팅 접근 권한이 없습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
