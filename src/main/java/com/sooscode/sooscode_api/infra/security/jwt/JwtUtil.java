package com.sooscode.sooscode_api.infra.security.jwt;

import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.status.AuthStatus;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * JWT 생성 · 파싱 · 검증을 담당하는 유틸 클래스
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expire}")
    private long accessTokenExpire;

    @Value("${jwt.refresh-token-expire}")
    private long refreshTokenExpire;

    private Key secretKey;

    /**
     * 의존성 주입 완료 후 secretKey 초기화
     * application.properties의 jwt.secret 값을 사용하여 Key 객체 생성
     */
    @PostConstruct
    private void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        log.info("JwtUtil 초기화 완료 - Access Token: {}ms, Refresh Token: {}ms",
                accessTokenExpire, refreshTokenExpire);
    }

    /**
     * Access Token 생성
     */
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserId())) // subject = userId
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpire))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpire))
                .signWith(secretKey)
                .compact();
    }


    /** userId 추출 */
    public Long getUserIdFromToken(String token) {
        String subject = getAllClaims(token).getSubject();
        return Long.valueOf(subject);
    }

    /** email 추출 */
    public String getEmailFromToken(String token) {
        return getAllClaims(token).get("email", String.class);
    }

    /** role 추출 */
    public String getRoleFromToken(String token) {
        return getAllClaims(token).get("role", String.class);
    }

    /** payload(Claims) 가져오기 */
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    /**
     * 토큰의 남은 만료 시간 (밀리초)
     * - 블랙리스트 TTL 설정 시 사용
     */
    public long getRemainingExpiration(String token) {
        try {
            Date expiration = getAllClaims(token).getExpiration();
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0;
        }
    }

    // ========== VALIDATION ==========
    /**
     * Access Token 검증 (서명 및 만료 체크)
     * - 유효하면 true 반환
     * - 만료된 토큰은 CustomException 발생 → 프론트에서 재발급 요청
     * - 그 외 잘못된 토큰은 false 반환 → 인증 실패 처리
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (ExpiredJwtException e) {
            // 만료된 토큰 → 재발급 필요하므로 예외 발생
            throw new CustomException(AuthStatus.ACCESS_TOKEN_EXPIRED);

        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT: {}", e.getMessage());
            return false;

        } catch (MalformedJwtException e) {
            log.warn("잘못된 JWT 형식: {}", e.getMessage());
            return false;

        } catch (SecurityException e) {
            log.warn("JWT 서명 불일치: {}", e.getMessage());
            return false;

        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 비어있음: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Refresh Token 만료 여부 체크 (예외 없이)
     * - RT 검증 시 사용 (만료되면 재발급이 아니라 재로그인 유도)
     * - true: 만료됨 또는 유효하지 않음
     * - false: 유효함
     */
    public boolean isTokenExpired(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return false;  // 유효함 = 만료 안 됨

        } catch (ExpiredJwtException e) {
            return true;  // 만료됨

        } catch (Exception e) {
            return true;  // 파싱 실패 = 유효하지 않음
        }
    }
}