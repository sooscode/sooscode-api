package com.sooscode.sooscode_api.global.api.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ValidStatus implements StatusCode {

    /**
     * User
     */
    // 일반 검증 실패
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALID_USR_001", "입력하신 값을 다시 확인해 주세요"),
    // 이름 검증
    NAME_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_USR_002", "이름을 입력해주세요"),
    NAME_TOO_SHORT(HttpStatus.BAD_REQUEST, "VALID_USR_003", "이름은 2자 이상이어야 합니다"),
    NAME_TOO_LONG(HttpStatus.BAD_REQUEST, "VALID_USR_004", "이름은 16자 이하이어야 합니다"),
    // 이메일 검증
    EMAIL_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_USR_005", "이메일을 입력해주세요"),
    EMAIL_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "VALID_USR_006", "올바른 이메일 형식이 아닙니다"),
    EMAIL_TOO_SHORT(HttpStatus.BAD_REQUEST, "VALID_USR_007", "이메일은 5자 이상이어야 합니다"),
    EMAIL_TOO_LONG(HttpStatus.BAD_REQUEST, "VALID_USR_008", "이메일은 50자 이하이어야 합니다"),
    // 비밀번호 검증
    PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_USR_009", "비밀번호를 입력해주세요"),
    PASSWORD_TOO_SHORT(HttpStatus.BAD_REQUEST, "VALID_USR_010", "비밀번호는 6자 이상이어야 합니다"),
    PASSWORD_TOO_LONG(HttpStatus.BAD_REQUEST, "VALID_USR_011", "비밀번호는 16자 이하이어야 합니다"),
    PASSWORD_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "VALID_USR_012", "비밀번호는 영문자와 숫자를 포함해야 합니다"),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "VALID_USR_013", "비밀번호가 일치하지 않습니다"),
    // 권한 검증
    ROLE_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_USR_014", "권한을 입력해주세요"),
    ADMIN_ROLE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "VALID_USR_015", "관리자 권한을 부여할 수 없습니다"),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "VALID_USR_016", "유효하지 않은 권한 값입니다"),

    /**
     * Class - 제목/설명
     */
    CLASS_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_CLA_001", "클래스 제목을 입력해주세요"),
    CLASS_TITLE_TOO_SHORT(HttpStatus.BAD_REQUEST, "VALID_CLA_002", "클래스 제목은 1자 이상이어야 합니다"),
    CLASS_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "VALID_CLA_003", "클래스 제목은 255자 이하여야 합니다"),
    CLASS_DESCRIPTION_TOO_LONG(HttpStatus.BAD_REQUEST, "VALID_CLA_004", "클래스 설명은 1000자 이하여야 합니다"),

    /**
     * Class - 날짜 (강의 운영 기간)
     */
    CLASS_START_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_CLA_010", "강의 시작일을 입력해주세요"),
    CLASS_END_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_CLA_011", "강의 종료일을 입력해주세요"),
    CLASS_START_DATE_PAST(HttpStatus.BAD_REQUEST, "VALID_CLA_012", "강의 시작일은 오늘 이후여야 합니다"),
    CLASS_END_DATE_BEFORE_START(HttpStatus.BAD_REQUEST, "VALID_CLA_013", "강의 종료일은 시작일 이후여야 합니다"),

    /**
     * Class - 시간 (매일 강의 시간대)
     */
    CLASS_START_TIME_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_CLA_020", "강의 시작 시간을 입력해주세요"),
    CLASS_END_TIME_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_CLA_021", "강의 종료 시간을 입력해주세요"),
    CLASS_END_TIME_BEFORE_START(HttpStatus.BAD_REQUEST, "VALID_CLA_022", "강의 종료 시간은 시작 시간 이후여야 합니다"),
    CLASS_DURATION_TOO_SHORT(HttpStatus.BAD_REQUEST, "VALID_CLA_023", "강의는 최소 30분 이상이어야 합니다"),
    CLASS_DURATION_TOO_LONG(HttpStatus.BAD_REQUEST, "VALID_CLA_024", "강의는 최대 12시간을 초과할 수 없습니다"),

    /**
     * Class - 기타
     */
    CLASS_IS_ONLINE_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_CLA_030", "온라인 여부를 선택해주세요"),
    CLASS_INSTRUCTOR_NOT_FOUND(HttpStatus.BAD_REQUEST, "VALID_CLA_031", "강사를 선택해주세요"),
    CLASS_STUDENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "VALID_CLA_032", "학생을 1명 이상 선택해주세요");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}