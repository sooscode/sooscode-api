package com.sooscode.sooscode_api.global.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sooscode.sooscode_api.global.api.status.GlobalStatus;
import com.sooscode.sooscode_api.global.api.status.StatusCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

/**
 * 통합 API 응답 클래스
 *
 * 사용법:
 * - 성공: return ApiResponse.ok(AdminStatus.CREATED, data);
 * - 실패: throw new CustomException(AdminStatus.NOT_FOUND);
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final int status;
    private final String code;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    @JsonIgnore  // JSON 응답에서 제외
    private final HttpStatus httpStatus;

    // ===== 성공 응답 =====

    /**
     * 데이터 없이 성공
     */
    public static ResponseEntity<ApiResponse<Void>> ok() {
        return ok(GlobalStatus.OK, null);
    }

    /**
     * 상태 코드만으로 성공
     */
    public static ResponseEntity<ApiResponse<Void>> ok(StatusCode statusCode) {
        return ok(statusCode, null);
    }

    /**
     * 데이터와 함께 성공 (기본 상태 코드)
     */
    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ok(GlobalStatus.OK, data);
    }

    /**
     * 상태 코드 + 데이터로 성공
     */
    public static <T> ResponseEntity<ApiResponse<T>> ok(StatusCode statusCode, T data) {
        ApiResponse<T> body = ApiResponse.<T>builder()
                .success(true)
                .status(statusCode.getHttpStatus().value())
                .code(statusCode.getCode())
                .message(statusCode.getMessage())
                .data(data)
                .timestamp(LocalDateTime.now())
                .httpStatus(statusCode.getHttpStatus())
                .build();

        return ResponseEntity
                .status(statusCode.getHttpStatus())
                .body(body);
    }

    // ===== 실패 응답 (ExceptionHandler에서 사용) =====

    /**
     * 상태 코드로 실패
     */
    public static ResponseEntity<ApiResponse<Void>> fail(StatusCode statusCode) {
        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .success(false)
                .status(statusCode.getHttpStatus().value())
                .code(statusCode.getCode())
                .message(statusCode.getMessage())
                .data(null)
                .timestamp(LocalDateTime.now())
                .httpStatus(statusCode.getHttpStatus())
                .build();

        return ResponseEntity
                .status(statusCode.getHttpStatus())
                .body(body);
    }

    /**
     * 상태 코드 + 상세 메시지로 실패
     */
    public static ResponseEntity<ApiResponse<Void>> fail(StatusCode statusCode, String detail) {
        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .success(false)
                .status(statusCode.getHttpStatus().value())
                .code(statusCode.getCode())
                .message(statusCode.getMessage() + " - " + detail)
                .data(null)
                .timestamp(LocalDateTime.now())
                .httpStatus(statusCode.getHttpStatus())
                .build();

        return ResponseEntity
                .status(statusCode.getHttpStatus())
                .body(body);
    }

    // ===== WebSocket용 (ResponseEntity 없이 객체만 반환) =====

    /**
     * WebSocket 실패 응답 (ResponseEntity 없이)
     */
    public static ApiResponse<Void> failBody(StatusCode statusCode) {
        return ApiResponse.<Void>builder()
                .success(false)
                .status(statusCode.getHttpStatus().value())
                .code(statusCode.getCode())
                .message(statusCode.getMessage())
                .data(null)
                .timestamp(LocalDateTime.now())
                .httpStatus(statusCode.getHttpStatus())
                .build();
    }
}