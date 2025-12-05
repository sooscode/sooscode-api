package com.sooscode.sooscode_api.global.jwt;

import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import com.sooscode.sooscode_api.global.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;

/**
 * JWT 기반 인증의 핵심 로직 (커스텀 필터)
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // 1) 쿠키에서 accessToken 찾기
        String token = extractAccessToken(request);

        // 2) 토큰 검증(토큰이 null인지? / 토큰이 만료되었는지?)
        if (token != null && jwtUtil.validateToken(token)) {

            // 3) email 추출
            String email = jwtUtil.getEmailFromToken(token);

            // 4) DB 조회 -> CustomUserDetails 만들기
            CustomUserDetails userDetails =
                    (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);

            // 5) 인증 객체 생성 후 시큐리티 컨텍스트에 저장
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private String extractAccessToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(c -> "accessToken".equals(c.getName()))
                .map(Cookie::getValue)
                .filter(v -> v != null && !v.isBlank() && !"null".equalsIgnoreCase(v))
                .findFirst()
                .orElse(null);
    }
}