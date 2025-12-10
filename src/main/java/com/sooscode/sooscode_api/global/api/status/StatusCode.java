package com.sooscode.sooscode_api.global.api.status;

import org.springframework.http.HttpStatus;

/**
 * 모든 상태 코드(성공/실패)를 담는 인터페이스
 * 도메인별 Enum에서 구현
 */
public interface StatusCode {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}