package com.sooscode.sooscode_api.application.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.sooscode.sooscode_api.application.auth.dto.*;
import com.sooscode.sooscode_api.application.auth.service.AuthServiceImpl;
import com.sooscode.sooscode_api.application.auth.service.GoogleAuthService;
import com.sooscode.sooscode_api.application.auth.dto.RegisterRequest;
import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;
    private final GoogleAuthService googleAuthService;

    // 로컬 로그인
    @PostMapping("/login")
    //public ResponseEntity<Void> login(
    public ResponseEntity<TokenResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResponse tokens = authService.loginUser(request);


//        // AccessToken → HttpOnly Cookie
//        ResponseCookie accessCookie = ResponseCookie.from("accessToken", tokens.getAccessToken())
//                .httpOnly(true)
//                .secure(false)
//                .path("/")
//                .sameSite("Lax")
//                .maxAge(30 * 60)
//                .build();

        // RefreshToken -> HttpOnly Cookie
        ResponseCookie refreshCookie  = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        //response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        //return ResponseEntity.ok().build();

        return ResponseEntity.ok(new TokenResponse(tokens.getAccessToken()));
    }

    // Google 로그인 URL로 redirect
    @GetMapping("/google/login")
    public void googleLogin(HttpServletResponse response) throws Exception {
        String url = googleAuthService.buildGoogleLoginUrl();
        response.sendRedirect(url);
    }

    // Google OAuth Callback
    @GetMapping("/google/callback")
    public ResponseEntity<?> googleCallback(
            @RequestParam("code") String code,
            HttpServletResponse response
    ) {
        LoginResponse tokens = googleAuthService.processGoogleCallback(code);

//        ResponseCookie accessCookie = ResponseCookie.from("accessToken", tokens.getAccessToken())
//                .httpOnly(true)
//                .secure(false)
//                .path("/")
//                .sameSite("Lax")
//                .maxAge(30 * 60)
//                .build();

        //response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        ResponseCookie refreshCookie  = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(7 * 24 * 60 * 60)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());


        return ResponseEntity
                .accepted()
                .body(new TokenResponse(tokens.getAccessToken()));


//        return ResponseEntity.status(HttpStatus.FOUND)
//                .location(URI.create("http://localhost:5173"))
//                .build();
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {

//        ResponseCookie deleteAccessCookie = ResponseCookie.from("accessToken","")
//                .httpOnly(true)
//                .secure(false)
//                .path("/")
//                .sameSite("Lax")
//                .maxAge(0)
//                .build();

        ResponseCookie deleteRefreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        //response.addHeader(HttpHeaders.SET_COOKIE, deleteAccessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString());
        return ResponseEntity.ok().build();
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {

        ApiResponse response = authService.registerUser(request);

        if (!response.isSuccess()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

        //return ResponseEntity.ok(authService.registerUser(request));
    }
}
