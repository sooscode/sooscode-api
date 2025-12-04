package com.sooscode.sooscode_api.global.config.websocket.interceptor;

import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.jwt.JwtUtil;
import com.sooscode.sooscode_api.global.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        log.info("=== preSend called ===");
        log.info("Command: {}", accessor != null ? accessor.getCommand() : "null");

        if (accessor != null) {
            // CONNECT: 초기 인증
            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                log.info("CONNECT command detected");

                String token = extractToken(accessor);
                log.info("Extracted token: {}", token != null ? "exists" : "null");

                if (token == null) {
                    log.error("Token is null");
                    throw new IllegalArgumentException("인증 토큰이 없습니다.");
                }

                if (!jwtUtil.validateToken(token)) {
                    log.error("Token validation failed");
                    throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
                }

                String email = jwtUtil.getUsernameFromToken(token);
                log.info("Email from token: {}", email);

                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> {
                            log.error("User not found: {}", email);
                            return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
                        });

                log.info("User found: userId={}, email={}, name={}",
                        user.getUserId(), email, user.getName());

                CustomUserDetails userDetails = new CustomUserDetails(user);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                accessor.setUser(authentication);

                // ✅ 세션 속성에 저장 (SEND/SUBSCRIBE에서 사용)
                if (accessor.getSessionAttributes() != null) {
                    accessor.getSessionAttributes().put("SPRING_SECURITY_CONTEXT", authentication);
                    log.info("✅ User stored in session attributes");
                }

                log.info("✅ WebSocket authenticated - userId: {}, email: {}, name: {}",
                        user.getUserId(), email, user.getName());
            }
            // SEND, SUBSCRIBE 등: 세션에서 인증 정보 복원
            else if (accessor.getUser() == null) {
                log.info("User is null for command: {}, attempting to restore from session", accessor.getCommand());

                if (accessor.getSessionAttributes() != null) {
                    Object storedAuth = accessor.getSessionAttributes().get("SPRING_SECURITY_CONTEXT");
                    if (storedAuth instanceof UsernamePasswordAuthenticationToken) {
                        accessor.setUser((UsernamePasswordAuthenticationToken) storedAuth);

                        CustomUserDetails userDetails = (CustomUserDetails) ((UsernamePasswordAuthenticationToken) storedAuth).getPrincipal();
                        log.info("✅ User restored from session - userId: {}", userDetails.getUser().getUserId());
                    } else {
                        log.error("❌ No authentication in session attributes");
                    }
                } else {
                    log.error("❌ Session attributes is null");
                }
            } else {
                log.info("User already present for command: {}", accessor.getCommand());
            }
        }

        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        log.info("Extracting token...");

        // 1. Authorization 헤더에서 추출
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("Token extracted from Authorization header");
            return token;
        }

        // 2. Cookie에서 추출
        String cookie = accessor.getFirstNativeHeader("Cookie");
        log.info("Cookie header: {}", cookie);

        if (cookie != null && cookie.contains("accessToken=")) {
            for (String c : cookie.split(";")) {
                String trimmed = c.trim();
                if (trimmed.startsWith("accessToken=")) {
                    String token = trimmed.substring("accessToken=".length());
                    log.info("Token extracted from Cookie");
                    return token;
                }
            }
        }

        log.warn("Token not found");
        return null;
    }
}