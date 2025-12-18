package com.sooscode.sooscode_api.global.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ClassRoomStatus implements StatusCode {

    // =========================
    // CLASS 관련
    // =========================
    CLASS_NOT_FOUND(HttpStatus.NOT_FOUND, "CLASS_001", "클래스를 찾을 수 없습니다"),
    CLASS_ACCESS_DENIED(HttpStatus.FORBIDDEN, "CLASS_002", "클래스 접근 권한이 없습니다"),
    CLASS_NOT_STARTED(HttpStatus.FORBIDDEN, "CLASS_003", "아직 시작되지 않은 클래스입니다"),
    CLASS_ALREADY_ENDED(HttpStatus.FORBIDDEN, "CLASS_004", "이미 종료된 클래스입니다"),
    CLASS_OFFLINE(HttpStatus.FORBIDDEN, "CLASS_005", "오프라인 클래스는 온라인 접속이 불가합니다"),
    CLASS_INVALID_STATUS(HttpStatus.BAD_REQUEST, "CLASS_006", "유효하지 않은 클래스 상태입니다"),
    CLASS_ALREADY_EXISTS(HttpStatus.CONFLICT, "CLASS_007", "이미 존재하는 클래스입니다"),
    CLASS_NOT_ACTIVE(HttpStatus.PAYMENT_REQUIRED, "CLASS_008", "정지된 클래스입니다. 관리자에게 문의하세요"),

    // =========================
    // PARTICIPANT (학생 참여)
    // =========================
    PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "PART_001", "해당 학생은 클래스에 참여하고 있지 않습니다"),
    PARTICIPANT_ALREADY_EXISTS(HttpStatus.CONFLICT, "PART_002", "이미 참여 중인 학생입니다"),
    PARTICIPANT_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "PART_003", "클래스 참여 인원이 초과되었습니다"),
    PARTICIPANT_CANNOT_REMOVE(HttpStatus.FORBIDDEN, "PART_004", "학생을 강제로 제거할 수 없습니다"),

    // =========================
    // COMMON VALIDATION
    // =========================
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 요청입니다"),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "COMMON_002", "필수 입력값이 누락되었습니다"),
    INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST, "COMMON_003", "유효하지 않은 시간 범위입니다"),

    INVALID_CLASS_ID(HttpStatus.BAD_REQUEST, "COMMON_004", "유효하지 않은 클래스 ID입니다"),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "COMMON_005", "유효하지 않은 사용자 ID입니다"),


    CLASS_STATUS_INVALID(HttpStatus.BAD_REQUEST, "CLASS_003", "진행중인 클래스는 삭제가 불가능합니다"),
    // =========================
    // 응답 성공 반환
    // =========================
    OK(HttpStatus.OK, "AUTH_000", "요청이 성공적으로 완료되었습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
