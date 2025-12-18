package com.sooscode.sooscode_api.global.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthStatus implements StatusCode {
    /**
     * 성공
     */
    LOGIN_SUCCESS(HttpStatus.OK, "AUTH_000", "로그인 성공"),
    LOGOUT_SUCCESS(HttpStatus.OK, "AUTH_001", "로그아웃 성공"),
    REGISTER_SUCCESS(HttpStatus.CREATED, "AUTH_002", "회원가입 성공"),
    EMAIL_VERIFY_SUCCESS(HttpStatus.OK, "AUTH_003", "이메일 인증 성공"),
    EMAIL_SEND_SUCCESS(HttpStatus.OK, "AUTH_004", "인증 이메일 발송 성공"),
    TOKEN_REISSUE_SUCCESS(HttpStatus.OK, "AUTH_005", "액세스 토큰 재발급 성공"),
    ME_SUCCESS(HttpStatus.OK, "AUTH_006", "내 정보 불러오기 성공"),


    /**
     * 이메일 / 회원가입 관련
     */
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_101", "등록되지 않은 이메일입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "AUTH_102", "이미 가입된 이메일입니다."),
    EMAIL_INACTIVE(HttpStatus.BAD_REQUEST, "AUTH_103", "탈퇴한 이메일입니다."),

    /**
     * 로그인 관련 (로컬)
     */
    PASSWORD_WRONG(HttpStatus.UNAUTHORIZED, "AUTH_201", "비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_202", "사용자를 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_203", "인증되지 않은 사용자입니다."),

    /**
     * 이메일 인증 관련
     */
    VERIFICATION_CODE_INVALID(HttpStatus.BAD_REQUEST, "AUTH_301", "인증 코드가 올바르지 않습니다."),
    VERIFICATION_EXPIRED(HttpStatus.GONE, "AUTH_302", "인증 코드가 만료되었습니다."),
    ERROR_WHILE_EMAIL_SENDING(HttpStatus.BAD_REQUEST, "AUTH_303", "이메일 전송에 실패했습니다."),

    /**
     * 토큰
     */
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_401", "액세스 토큰이 만료되었습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_402", "리프레시 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_403", "리프레시 토큰이 만료되었습니다."),

    /**
     * Google OAuth 관련
     */
    GOOGLE_ACCESS_TOKEN_FAILED(HttpStatus.BAD_REQUEST, "AUTH_501", "구글 액세스 토큰 요청 실패"),
    GOOGLE_USER_INFO_FAILED(HttpStatus.BAD_REQUEST, "AUTH_502", "구글 사용자 정보 조회 실패"),

    /**
     *  비밀번호 변경 관련
     */
    PASSWORD_NOT_SAME_AS_CURRENT(HttpStatus.BAD_REQUEST, "AUTH_601", "현재 비밀번호가 일치하지 않습니다."),
    PASSWORD_CANNOT_BE_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "AUTH_602", "새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}