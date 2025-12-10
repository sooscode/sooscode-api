package com.sooscode.sooscode_api.global.api.status;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum CodeValidStatus implements StatusCode {

    // 기본 검증 실패
    CODE_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALID_001", "코드를 다시 확인해 주세요"),

    // 빈 코드
    CODE_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_002", "코드를 입력해주세요"),
    CODE_TOO_SHORT(HttpStatus.BAD_REQUEST, "VALID_003", "코드가 비어 있거나 너무 짧습니다"),

    // 코드 길이 제한
    CODE_TOO_LONG(HttpStatus.BAD_REQUEST, "VALID_004", "코드 길이가 제한을 초과했습니다 (100KB 이하)"),

    // 인코딩
    CODE_INVALID_ENCODING(HttpStatus.BAD_REQUEST, "VALID_005", "코드에 허용되지 않는 문자가 포함되어 있습니다"),

    // Main 클래스 검증
    MAIN_CLASS_REQUIRED(HttpStatus.BAD_REQUEST, "VALID_006", "public class Main 형식을 포함해야 합니다"),
    MAIN_CLASS_INVALID(HttpStatus.BAD_REQUEST, "VALID_007", "클래스명은 반드시 Main 이어야 합니다"),

    // 줄 수
    CODE_TOO_MANY_LINES(HttpStatus.BAD_REQUEST, "VALID_010", "코드 줄 수가 제한을 초과했습니다"),
    CODE_TOO_MANY_IMPORTS(HttpStatus.BAD_REQUEST, "VALID_011", "사용된 import 문이 너무 많습니다"),

    // 파일 처리 관련
    FILE_WRITE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "VALID_012", "코드 파일 생성 중 오류가 발생했습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
