package com.sooscode.sooscode_api.global.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TokenStatus implements StatusCode {

    // ===== JWT 관련 기본 오류 =====
    NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN_001", "토큰이 존재하지 않습니다"),
    EXPIRED(HttpStatus.FORBIDDEN, "TOKEN_002", "토큰이 만료되었습니다"),
    INVALID(HttpStatus.FORBIDDEN, "TOKEN_003", "유효하지 않은 토큰입니다"),
    SIGNATURE(HttpStatus.FORBIDDEN, "TOKEN_004", "토큰 서명이 올바르지 않습니다"),
    MALFORMED(HttpStatus.BAD_REQUEST, "TOKEN_005", "잘못된 형식의 토큰입니다"),
    UNSUPPORTED(HttpStatus.BAD_REQUEST, "TOKEN_006", "지원되지 않는 토큰 형식입니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
