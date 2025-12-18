package com.sooscode.sooscode_api.global.exception;

import com.sooscode.sooscode_api.global.response.ApiResponse;
import com.sooscode.sooscode_api.global.status.GlobalStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;

/**
 * 전역 예외 처리 핸들러
 * 모든 @RestController에 적용
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CustomException 처리
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        log.error("[CustomException] 코드: {}, 메시지: {}",
                e.getStatusCode().getCode(), e.getMessage());

        return ApiResponse.fail(e.getStatusCode());
    }

    /**
     * Bean Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("유효성 검증 실패");

        log.error("[ValidationException] 메시지: {}", errorMessage);

        return ApiResponse.fail(GlobalStatus.VALIDATION_FAILED, errorMessage);
    }

    /**
     * 컨트롤러에 존재하지 않는 요청
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResource(
            NoResourceFoundException e
    ) {
        return ApiResponse.fail(GlobalStatus.BAD_REQUEST);
    }

    /**
     * 예상하지 못한 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("[Exception] 예상치 못한 에러 발생", e);

        return ApiResponse.fail(GlobalStatus.INTERNAL_SERVER_ERROR);
    }
}