package com.sooscode.sooscode_api.global.api.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * ApiResponse의 httpStatus를 실제 HTTP 응답 상태 코드로 매핑
 * 컨트롤러에서 ResponseEntity 없이 ApiResponse만 반환해도 됨
 */
@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // ApiResponse 타입일 때만 적용
        return ApiResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (body instanceof ApiResponse<?> apiResponse) {
            // ApiResponse의 httpStatus를 실제 HTTP 상태 코드로 설정
            response.setStatusCode(apiResponse.getHttpStatus());
        }

        return body;
    }
}