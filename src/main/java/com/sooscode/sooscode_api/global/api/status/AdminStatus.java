package com.sooscode.sooscode_api.global.api.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AdminStatus implements StatusCode {

    // ===== 관리자 공통 (ADM_COM) =====
    OK(HttpStatus.OK, "ADM_COM_000", "성공적으로 요청을 처리하였습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "ADM_COM_001", "관리자를 찾을 수 없습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "ADM_COM_002", "관리자 권한이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "ADM_COM_003", "해당 기능에 접근할 수 없습니다"),

    // ===== 사용자 관리 (ADM_USR) =====
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "ADM_USR_001", "사용자를 찾을 수 없습니다"),
    USER_FORBIDDEN_ADMIN_DELETE(HttpStatus.FORBIDDEN, "ADM_USR_002", "관리자 계정은 삭제할 수 없습니다"),
    USER_FORBIDDEN_ADMIN_MODIFY(HttpStatus.FORBIDDEN, "ADM_USR_003", "관리자 계정은 수정할 수 없습니다"),
    USER_FORBIDDEN_ROLE_TO_ADMIN(HttpStatus.FORBIDDEN, "ADM_USR_004", "관리자 역할로 변경할 수 없습니다"),
    USER_CSV_PARSE_ERROR(HttpStatus.BAD_REQUEST, "ADM_USR_005", "CSV 파일 처리 중 오류가 발생했습니다"),
    USER_EXCEL_EXPORT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ADM_USR_006", "엑셀 파일 생성 중 오류가 발생했습니다"),
    USER_INVALID_NAME(HttpStatus.BAD_REQUEST, "ADM_USR_007", "유효하지 않은 사용자 이름입니다"),
    USER_INVALID_EMAIL(HttpStatus.BAD_REQUEST, "ADM_USR_008", "유효하지 않은 이메일 형식입니다"),
    USER_INVALID_ROLE(HttpStatus.BAD_REQUEST, "ADM_USR_009", "유효하지 않은 역할입니다"),
    USER_BLOCK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADM_USR_010", "사용자 차단 처리 중 오류가 발생했습니다"),
    USER_FORCE_LOGOUT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADM_USR_011", "사용자를 강제 로그아웃할 수 없습니다"),

    // Excel 파일 검증 (ADM_USR_1XX)
    USER_EXCEL_EMPTY(HttpStatus.BAD_REQUEST, "ADM_USR_100", "파일이 비어있습니다"),
    USER_EXCEL_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "ADM_USR_101", "파일 크기가 5MB를 초과합니다"),
    USER_EXCEL_INVALID_NAME(HttpStatus.BAD_REQUEST, "ADM_USR_102", "유효하지 않은 파일명입니다"),
    USER_EXCEL_INVALID_EXTENSION(HttpStatus.BAD_REQUEST, "ADM_USR_103", "Excel 파일(.xlsx, .xls)만 업로드 가능합니다"),
    USER_EXCEL_INVALID_TYPE(HttpStatus.BAD_REQUEST, "ADM_USR_104", "허용되지 않은 파일 형식입니다"),
    USER_EXCEL_DANGEROUS_FILE(HttpStatus.BAD_REQUEST, "ADM_USR_105", "보안상 허용되지 않는 파일입니다"),
    USER_EXCEL_INVALID_HEADER(HttpStatus.BAD_REQUEST, "ADM_USR_106", "Excel 헤더 형식이 올바르지 않습니다 (email, name 필수)"),
    USER_EXCEL_TOO_MANY_ROWS(HttpStatus.BAD_REQUEST, "ADM_USR_107", "최대 1000건까지 일괄 등록 가능합니다"),
    USER_EXCEL_NO_DATA(HttpStatus.BAD_REQUEST, "ADM_USR_108", "등록할 데이터가 없습니다"),
    USER_EXCEL_PARSE_ERROR(HttpStatus.BAD_REQUEST, "ADM_USR_109", "Excel 파일 처리 중 오류가 발생했습니다"),
    USER_EXCEL_INVALID_STRUCTURE(HttpStatus.BAD_REQUEST, "ADM_USR_110", "Excel 파일 구조가 올바르지 않습니다"),

    // ===== 클래스 관리 (ADM_CLS) =====
    CLASS_CREATE_SUCCESS(HttpStatus.OK, "ADM_CLS_000", "클래스 생성에 성공하였습니다"),
    CLASS_NOT_FOUND(HttpStatus.NOT_FOUND, "ADM_CLS_001", "클래스를 찾을 수 없습니다"),
    CLASS_INSTRUCTOR_INVALID(HttpStatus.BAD_REQUEST, "ADM_CLS_002", "강사가 아닌 사용자를 등록할 수 없습니다"),
    CLASS_INSTRUCTOR_DUPLICATED(HttpStatus.BAD_REQUEST, "ADM_CLS_003", "이미 등록되어 있는 강사입니다"),
    CLASS_FORCE_CLOSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADM_CLS_004", "클래스를 강제로 종료할 수 없습니다"),

    // ===== 시스템 설정 (ADM_SYS) =====
    SETTING_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADM_SYS_001", "시스템 설정을 불러오지 못했습니다"),
    SETTING_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADM_SYS_002", "시스템 설정 저장 중 오류가 발생했습니다"),
    SERVER_METRIC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ADM_SYS_003", "서버 상태 정보를 가져올 수 없습니다"),
    SERVER_SHUTDOWN_DENIED(HttpStatus.FORBIDDEN, "ADM_SYS_004", "서버 종료 권한이 없습니다"),

    // ===== 로그 관리 (ADM_LOG) =====
    LOG_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ADM_LOG_001", "로그 접근 권한이 없습니다"),
    LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "ADM_LOG_002", "요청한 로그를 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}