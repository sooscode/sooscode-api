package com.sooscode.sooscode_api.global.api.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthStatus implements StatusCode {

    /**
     * 이메일 / 회원가입 관련
     */
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_001", "등록되지 않은 이메일입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "AUTH_002", "이미 가입된 이메일입니다."),
    EMAIL_INACTIVE(HttpStatus.BAD_REQUEST, "AUTH_003", "탈퇴한 이메일입니다."),

    /**
     * 로그인 관련 (로컬)
     */
    PASSWORD_WRONG(HttpStatus.UNAUTHORIZED, "AUTH_004", "비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_005", "해당 사용자를 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_006", "인증되지 않은 사용자입니다."),

    /**
     * 이메일 인증 관련
     */
    VERIFICATION_CODE_INVALID(HttpStatus.BAD_REQUEST, "AUTH_007", "인증 코드가 올바르지 않습니다."),
    VERIFICATION_EXPIRED(HttpStatus.GONE, "AUTH_008", "인증 코드가 만료되었습니다."),

    ERROR_WHILE_EMAIL_SENDING(HttpStatus.BAD_REQUEST, "AUTH_009", "이메일 전송에 실패했습니다."),

    /**
     * AT 토큰
     */
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH-004", "액세스 토큰이 만료되었습니다."),

    /**
     * Refresh Token 관련
     */
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_010", "리프레시 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_011", "리프레시 토큰이 만료되었습니다."),

    /**
     * Google OAuth 관련
     */
    GOOGLE_ACCESS_TOKEN_FAILED(HttpStatus.BAD_REQUEST, "AUTH_012", "구글 액세스 토큰 요청에 실패했습니다."),
    GOOGLE_ACCESS_TOKEN_PARSING_FAILED(HttpStatus.BAD_REQUEST, "AUTH_013", "구글 액세스 토큰 파싱에 실패했습니다."),
    GOOGLE_USER_INFO_FAILED(HttpStatus.BAD_REQUEST, "AUTH_014", "구글 사용자 정보를 조회할 수 없습니다."),
    GOOGLE_USER_INFO_PARSING_FAILED(HttpStatus.BAD_REQUEST, "AUTH_015", "구글 사용자 정보 파싱에 실패했습니다."),


    /**
     *  비밀번호 변경 관련
     */
    PASSWORD_NOT_SAME_AS_CURRENT(HttpStatus.BAD_REQUEST, "AUTH_016", "현재 비밀번호가 일치하지 않습니다."),
    PASSWORD_CANNOT_BE_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "AUTH_015", "새 비밀번호는 현재 비밀번호와 동일할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}