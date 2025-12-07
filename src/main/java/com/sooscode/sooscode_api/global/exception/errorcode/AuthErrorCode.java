package com.sooscode.sooscode_api.global.exception.errorcode;

import com.sooscode.sooscode_api.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH_001", "존재하지 않는 이메일입니다"),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "AUTH_002", "이미 가입한 이메일입니다"),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_003", "이메일 또는 비밀번호가 올바르지 않습니다"),
    VERIFICATION_CODE_INVALID(HttpStatus.BAD_REQUEST, "AUTH_004", "인증 코드가 올바르지 않습니다"),
    VERIFICATION_EXPIRED(HttpStatus.FORBIDDEN, "AUTH_005", "인증 요청이 만료되었습니다"),
    ERROR_WHILE_EMAIL_SENDING(HttpStatus.BAD_REQUEST, "AUTH_006", "이메일 전송에 실패했습니다"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH_007", "리프레시 토큰을 찾을 수 없습니다"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "AUTH_008", "리프레시 토큰이 만료되었습니다"),
    EMAIL_INACTIVE(HttpStatus.BAD_REQUEST, "USER_009", "이미 탈퇴한 이메일입니다.");



    private final HttpStatus status;
    private final String code;
    private final String message;
}