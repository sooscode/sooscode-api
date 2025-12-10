package com.sooscode.sooscode_api.global.api.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AdminStatus implements StatusCode {

    // ===== 관리자 공통 =====
    NOT_FOUND(HttpStatus.NOT_FOUND, "ADMIN_001", "관리자를 찾을 수 없습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "ADMIN_002", "관리자 권한이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "ADMIN_003", "해당 기능에 접근할 수 없습니다"),

    CLASS_CREATE_SUCCESS(HttpStatus.OK, "ADMIN_CLASS_200", "클래스 생성에 성공하였습니다"),

    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "ADMIN_USER_010", "유저가 존재하지 않습니다"),
    CLASS_INSTRUCTOR_INVALID(HttpStatus.BAD_REQUEST, "ADMIN_CLS_003", "이 유저는 강사가 아닙니다"),

    // ===== 강제 종료, 유저 관리 =====
    CLASS_FORCE_CLOSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADMIN_010", "클래스를 강제로 종료할 수 없습니다"),
    FORCE_LOGOUT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADMIN_011", "사용자를 강제 로그아웃할 수 없습니다"),
    USER_BLOCK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADMIN_012", "사용자 차단 처리 중 오류가 발생했습니다"),

    // ===== 시스템 설정 =====
    SETTING_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADMIN_020", "시스템 설정을 불러오지 못했습니다"),
    SETTING_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADMIN_021", "시스템 설정 저장 중 오류가 발생했습니다"),

    // ===== 모니터링 / 서버 관리 =====
    SERVER_METRIC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADMIN_030", "서버 상태 정보를 가져올 수 없습니다"),
    SERVER_SHUTDOWN_DENIED(HttpStatus.FORBIDDEN, "ADMIN_031", "서버 종료 권한이 없습니다"),

    // ===== 보고서 / 로그 =====
    LOG_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ADMIN_040", "로그 접근 권한이 없습니다"),
    LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "ADMIN_041", "요청한 로그를 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
