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
    // 룰 검증
    ROLE_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_USR_014", "권한을 입력해주세요"),
    ADMIN_ROLE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "VALID_USR_014", "관리자 권한을 부여할 수 없습니다"),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "VALID_USR_015", "유효하지 않은 권한 값입니다"),
    /**
     * Class
     */
    // 클래스 제목 검증
    CLASS_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_CLA_001", "클래스 제목을 입력해주세요"),
    CLASS_TITLE_TOO_SHORT(HttpStatus.BAD_REQUEST, "VALID_CLA_002", "클래스 제목은 1자 이상이어야 합니다"),
    CLASS_TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "VALID_CLA_003", "클래스 제목은 255자 이하여야 합니다"),
    // 클래스 설명 검증
    CLASS_DESCRIPTION_TOO_LONG(HttpStatus.BAD_REQUEST, "VALID_CLA_004", "클래스 설명은 1000자 이하여야 합니다"),
    // 클래스 시간 검증
    CLASS_START_TIME_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_CLA_005", "시작 시간을 입력해주세요"),
    CLASS_END_TIME_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_CLA_006", "종료 시간을 입력해주세요"),
    CLASS_END_TIME_BEFORE_START(HttpStatus.BAD_REQUEST, "VALID_CLA_007", "종료 시간은 시작 시간보다 이후여야 합니다"),
    CLASS_START_TIME_PAST(HttpStatus.BAD_REQUEST, "VALID_CLA_008", "시작 시간은 현재보다 미래여야 합니다"),
    CLASS_DURATION_TOO_SHORT(HttpStatus.BAD_REQUEST, "VALID_CLA_009", "클래스는 최소 30분 이상이어야 합니다"),
    CLASS_DURATION_TOO_LONG(HttpStatus.BAD_REQUEST, "VALID_CLA_010", "클래스는 최대 24시간을 초과할 수 없습니다"),
    // 클래스 온라인 여부 검증
    CLASS_IS_ONLINE_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_CLA_011", "온라인 여부를 선택해주세요"),
    // 클래스에 강사 및 학생 등록 검증
    CLASS_INSTRUCTOR_NOT_FOUND(HttpStatus.BAD_REQUEST, "VALID_CLA_012", "등록할 강사를 입력해주세요"),
    CLASS_STUDENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "VALID_CLA_013", "등록할 학생을 1명 이상 입력해주세요");



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}