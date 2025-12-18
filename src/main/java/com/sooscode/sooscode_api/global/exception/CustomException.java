package com.sooscode.sooscode_api.global.exception;

import com.sooscode.sooscode_api.global.status.StatusCode;
import lombok.Getter;

/**
 * 커스텀 예외 클래스
 * StatusCode를 기반으로 예외 정보 전달
 */
@Getter
public class CustomException extends RuntimeException {

    private final StatusCode statusCode;

    /**
     * 기본 생성자
     */
    public CustomException(StatusCode statusCode) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
    }

    /**
     * 상세 메시지 추가 생성자
     */
    public CustomException(StatusCode statusCode, String detail) {
        super(statusCode.getMessage() + " - " + detail);
        this.statusCode = statusCode;
    }

    /**
     * 원인 예외 포함 생성자
     */
    public CustomException(StatusCode statusCode, Throwable cause) {
        super(statusCode.getMessage(), cause);
        this.statusCode = statusCode;
    }

    /**
     * 상세 메시지 + 원인 예외 포함 생성자
     */
    public CustomException(StatusCode statusCode, String detail, Throwable cause) {
        super(statusCode.getMessage() + " - " + detail, cause);
        this.statusCode = statusCode;
    }
}