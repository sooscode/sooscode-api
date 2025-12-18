package com.sooscode.sooscode_api.global.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 전역 공통 상태 코드
 */
@Getter
@RequiredArgsConstructor
public enum GlobalStatus implements StatusCode {

    // ===== 성공 =====
    OK(HttpStatus.OK, "GLOBAL_000", "요청이 성공적으로 처리되었습니다"),
    CREATED(HttpStatus.CREATED, "GLOBAL_001", "리소스가 성공적으로 생성되었습니다"),

    // ===== 클라이언트 오류 =====
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "GLOBAL_400", "잘못된 요청입니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "GLOBAL_401", "인증이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "GLOBAL_403", "접근 권한이 없습니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "GLOBAL_404", "요청한 리소스를 찾을 수 없습니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "GLOBAL_405", "허용되지 않은 HTTP 메서드입니다"),
    CONFLICT(HttpStatus.CONFLICT, "GLOBAL_409", "리소스 충돌이 발생했습니다"),

    // ===== 유효성 검증 =====
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "GLOBAL_410", "입력값 검증에 실패했습니다"),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "GLOBAL_411", "유효하지 않은 파라미터입니다"),

    // ===== 서버 오류 =====
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL_500", "서버 내부 오류가 발생했습니다"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "GLOBAL_503", "서비스를 일시적으로 사용할 수 없습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}