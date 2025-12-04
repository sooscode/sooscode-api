// global/exception/ErrorCode.java
package com.sooscode.sooscode_api.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ===== 공통 =====
    OK(HttpStatus.OK, "COMMON_000", "요청을 성공적으로 처리했습니다"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 요청입니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_002", "인증이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_003", "접근 권한이 없습니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_004", "요청한 리소스를 찾을 수 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_005", "서버 오류가 발생했습니다"),

    // ===== 검증 =====
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALID_001", "입력하신 값을 다시 확인해 주세요"),

    // ===== 인증/인가 =====
    AUTH_EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH_001", "존재하지 않는 이메일입니다"),
    AUTH_DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "AUTH_002", "이미 가입한 이메일입니다"),
    AUTH_LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_003", "이메일과 비밀번호가 일치하지 않습니다"),
    AUTH_VERIFICATION_CODE_INVALID(HttpStatus.BAD_REQUEST, "AUTH_004", "인증 코드가 일치하지 않습니다"),
    AUTH_VERIFICATION_EXPIRED(HttpStatus.FORBIDDEN, "AUTH_005", "인증 요청이 만료되었습니다"),
    AUTH_VERIFICATION_ALREADY_EXIST(HttpStatus.FORBIDDEN, "AUTH_006", "인증 요청이 이미 존재합니다"),

    // ===== JWT =====
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN_001", "로그인이 필요한 서비스입니다"),
    TOKEN_INVALID(HttpStatus.FORBIDDEN, "TOKEN_002", "유효하지 않은 토큰입니다. 다시 로그인해 주세요"),
    TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "TOKEN_003", "토큰이 만료되었습니다. 다시 로그인해 주세요"),
    TOKEN_SIGNATURE_INVALID(HttpStatus.FORBIDDEN, "TOKEN_004", "잘못된 토큰 서명입니다"),

    // ===== 사용자 =====
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다"),
    USER_SUSPENDED(HttpStatus.FORBIDDEN, "USER_002", "정지된 사용자입니다"),
    USER_WITHDRAWN(HttpStatus.FORBIDDEN, "USER_003", "탈퇴한 사용자입니다"),

    // ===== 클래스 =====
    CLASS_NOT_FOUND(HttpStatus.NOT_FOUND, "CLASS_001", "클래스를 찾을 수 없습니다"),
    CLASS_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CLASS_002", "클래스 접근 권한이 없습니다"),
    CLASS_NOT_STARTED(HttpStatus.FORBIDDEN, "CLASS_003", "아직 시작되지 않은 클래스입니다"),
    CLASS_ALREADY_ENDED(HttpStatus.FORBIDDEN, "CLASS_004", "이미 종료된 클래스입니다"),
    CLASS_OFFLINE(HttpStatus.FORBIDDEN, "CLASS_005", "오프라인 클래스는 접속할 수 없습니다"),
    CLASS_STATUS_INVALID(HttpStatus.BAD_REQUEST, "CLASS_006", "잘못된 클래스 상태입니다"),

    // ===== 채팅 =====
    CHAT_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CHAT_001", "채팅 저장에 실패했습니다"),
    CHAT_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_002", "채팅을 찾을 수 없습니다"),
    CHAT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHAT_003", "채팅 내역을 볼 권한이 없습니다"),

    // ===== 코드 컴파일 =====
    CODE_COMPILE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CODE_001", "코드 컴파일에 실패했습니다"),
    CODE_EXECUTION_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "CODE_002", "코드 실행 시간이 초과되었습니다"),
    CODE_SECURITY_VIOLATION(HttpStatus.BAD_REQUEST, "CODE_003", "보안상 제한된 코드입니다"),
    CODE_RUNTIME_ERROR(HttpStatus.BAD_REQUEST, "CODE_004", "코드 실행 중 오류가 발생했습니다"),
    CODE_SERVER_CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "CODE_005", "컴파일 서버가 응답하지 않습니다"),

    // ===== 스냅샷 =====
    SNAPSHOT_NOT_FOUND(HttpStatus.NOT_FOUND, "SNAPSHOT_001", "스냅샷을 찾을 수 없습니다"),
    SNAPSHOT_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SNAPSHOT_002", "스냅샷 저장에 실패했습니다"),

    // ===== 파일 =====
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE_001", "파일을 찾을 수 없습니다"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_002", "파일 업로드에 실패했습니다"),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "FILE_003", "파일 크기가 제한을 초과했습니다"),
    FILE_TYPE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "FILE_004", "허용되지 않는 파일 형식입니다"),

    // ===== LiveKit =====
    LIVEKIT_TOKEN_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "LIVEKIT_001", "LiveKit 토큰 생성에 실패했습니다"),
    LIVEKIT_CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "LIVEKIT_002", "LiveKit 연결에 실패했습니다"),

    // ===== LiveKit =====
    PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "PARTICIPANT_001", "참가자를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}