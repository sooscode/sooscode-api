package com.sooscode.sooscode_api.global.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserStatus implements StatusCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다"),
    SUSPENDED(HttpStatus.FORBIDDEN, "USER_002", "정지된 사용자입니다"),
    INVALID_ROLE(HttpStatus.NOT_FOUND, "USER_003", "옳바르지 않은 권한입니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}