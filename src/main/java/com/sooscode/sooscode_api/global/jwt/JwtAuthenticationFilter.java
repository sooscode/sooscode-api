package com.sooscode.sooscode_api.global.jwt;

import com.sooscode.sooscode_api.global.user.CustomUserDetails;
import com.sooscode.sooscode_api.global.user.CustomUserDetailsService;
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
                                    FilterChain filterChain)
            throws ServletException, IOException {

        /**
         * 쿠키에서 AT 꺼냄
         */
        String token = null;
        if (request.getCookies() != null) {
            token = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("accessToken"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        /**
         * JWT 유효성 검증
         */
        if (token != null && jwtUtil.validateToken(token)) {
            /**
             * 유효하면 토큰에서 email 추출
             */
            String email = jwtUtil.getUsernameFromToken(token);

            /**
             * DB에서 유저 로드 -> userDetails 생성
             */
            CustomUserDetails userDetails =
                    (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            /**
             * SecurityContextHolder에 인증 저장
             */
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
