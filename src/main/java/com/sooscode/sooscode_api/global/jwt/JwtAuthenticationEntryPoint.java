package com.sooscode.sooscode_api.global.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sooscode.sooscode_api.application.auth.dto.SBApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증되지 않은 사용자(로그인X)가 보호된 리소스에 접근할 때 호출되는 핸들러
 * 401 설정
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        log.warn("Unauthorized access: {} - {}", request.getRequestURI(), authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        SBApiResponse SBApiResponse = new SBApiResponse(false, "인증이 필요합니다.", null);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(SBApiResponse));
    }
}
