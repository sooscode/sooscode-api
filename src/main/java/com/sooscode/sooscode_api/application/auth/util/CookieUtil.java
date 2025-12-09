package com.sooscode.sooscode_api.application.auth.util;

import com.sooscode.sooscode_api.application.auth.dto.LoginResponse;
import com.sooscode.sooscode_api.application.auth.dto.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public class CookieUtil {

    /**
     * AT RT 쿠키 생성
     */
    public static void addTokenCookies(
            HttpServletResponse response,
            TokenResponse tokens,
            boolean rememberMe
    ) {
        int refreshTokenExpire = rememberMe
                ? 60 * 60 * 24 * 30
                : 60 * 60 * 24 * 7;

        // access token 쿠키
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", tokens.getAccessToken())
                .httpOnly(true)
                .path("/")
                .maxAge(60 * 30)
                .sameSite("Lax")
                .build();

        // refresh token 쿠키
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpire)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }



    /**
     * AT RT 쿠키 삭제
     */
    public static void deleteTokenCookies(HttpServletResponse response, LoginResponse tokens) {
        ResponseCookie deleteAccessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        ResponseCookie deleteRefreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteAccessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString());
    }

    /**
     * RT 가져오기
     */
    public static String getRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("refreshToken")) {
                return cookie.getValue();
            }
        }
        return null;
    }


}
