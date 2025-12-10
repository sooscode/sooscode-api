package com.sooscode.sooscode_api.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sooscode.sooscode_api.application.auth.dto.SBApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증된 사용자가 권한(Role)이 부족할 때 호출되는 핸들러
 * ex) ROLE_STUDENT는 /admin/** 접근 못함
 * 403 설정
 */
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        log.warn("Access denied: {} - {}", request.getRequestURI(), accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        SBApiResponse SBApiResponse = new SBApiResponse(false, "접근 권한이 없습니다.", null);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(SBApiResponse));
    }
}
