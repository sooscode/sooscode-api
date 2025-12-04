package com.sooscode.sooscode_api.global.jwt;

import com.sooscode.sooscode_api.domain.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT 생성 · 파싱 · 검증을 담당하는 유틸 클래스
 */
@Component
public class JwtUtil {

    // TOKEN SETTINGS
    private static final long ACCESS_TOKEN_EXPIRE = 30 * 60 * 1000L;        // 30분
    private static final long REFRESH_TOKEN_EXPIRE = 7 * 24 * 60 * 60 * 1000L; // 7일

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);


    // TOKEN GENERATION
    /** Access Token 생성 */
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserId())) // subject = userId
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE))
                .signWith(secretKey)
                .compact();
    }

    /** Refresh Token 생성 */
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE))
                .signWith(secretKey)
                .compact();
    }


    //   PARSE & GET CLAIMS
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


    //   VALIDATION
    /** 서명 및 만료 체크 */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (ExpiredJwtException e) {
            System.out.println("JWT 만료됨");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT");
        } catch (MalformedJwtException e) {
            System.out.println("잘못된 JWT");
        } catch (SecurityException e) {
            System.out.println("JWT 서명 불일치");
        } catch (Exception e) {
            System.out.println("JWT 검증 실패");
        }

        return false;
    }
}