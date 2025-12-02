package com.sooscode.sooscode_api.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private int status;
    private LocalDateTime timestamp;

    // ErrorCode로부터 생성하는 정적 메서드
    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .status(errorCode.getStatus().value())
                .timestamp(LocalDateTime.now())
                .build();
    }
}