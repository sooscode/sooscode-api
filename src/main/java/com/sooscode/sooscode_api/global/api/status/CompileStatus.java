package com.sooscode.sooscode_api.global.api.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CompileStatus implements StatusCode {

    // ok = ok 라는 뜻
    OK(HttpStatus.OK, "COMPILE_000", "컴파일 완료"),

    NOT_FOUND(HttpStatus.NOT_FOUND, "COMPILE_001", "코드를 입력해주세요"),
    // 코드 공백 제출
    EMPTY_CODE(HttpStatus.BAD_REQUEST, "COMPILE_002", "코드가 입력되지 않았습니다"),
    // 블랙리스트 코드 제출
    FORBIDDEN_SYNTAX(HttpStatus.BAD_REQUEST,"COMPILE_003","허용되지 않은 코드가 포함되어 있습니다"),
    //컴파이서버랑 통신 안됨
    WORKER_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE,"COMPILE_004","컴파일 서버와 통신할 수 없습니다"),
    //컴파일서버랑 연결 타임아웃
    WORKER_TIMEOUT(HttpStatus.REQUEST_TIMEOUT,"COMPILE_005","컴파일 시간이 초과되었습니다"),
    // 컴파일서버 처리중 오류
    WORKER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"COMPILE_006","컴파일 서버 처리 중 오류가 발생했습니다"),
    // 컴파일 오류
    COMPILE_FAILED(HttpStatus.BAD_REQUEST,"COMPILE_007","컴파일 오류가 발생했습니다"),
    //코드 실행중 오류
    RUNTIME_ERROR(HttpStatus.BAD_REQUEST,"COMPILE_008","코드 실행 중 오류가 발생했습니다");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
