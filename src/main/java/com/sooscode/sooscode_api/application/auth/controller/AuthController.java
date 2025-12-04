package com.sooscode.sooscode_api.application.auth.controller;

import com.sooscode.sooscode_api.application.auth.util.CookieUtil;
import com.sooscode.sooscode_api.application.user.dto.UserInfo;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.global.jwt.JwtUtil;
import com.sooscode.sooscode_api.global.user.CustomUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.sooscode.sooscode_api.application.auth.dto.*;
import com.sooscode.sooscode_api.application.auth.service.AuthServiceImpl;
import com.sooscode.sooscode_api.application.auth.dto.RegisterRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * 인증 및 회원가입/로그인 로직 처리 서비스
     */
    private final AuthServiceImpl authService;
    /**
     * 스프링 시큐리티 인증 처리 매치너 (FormLogin 대체)
     */
    private final AuthenticationManager authenticationManager;
    /**
     * JWT 생성/검증 유틸리티
     */
    private final JwtUtil jwtUtil;

    /**
     * 로컬 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResponse data = authService.authenticateAndGenerateTokens(request, authenticationManager);
        CookieUtil.addTokenCookies(response, data);
        return ResponseEntity.ok(new ApiResponse(true, "로그인 성공", data));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletResponse response) {
       CookieUtil.deleteTokenCookies(response, null);
       ApiResponse responseBody = new ApiResponse(true, "로그아웃 성공", null);
       return ResponseEntity.ok(responseBody);
    }

    /**
     * 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        RegisterResponse data = authService.registerUser(request);
        ApiResponse responseBody = new ApiResponse(true, "회원가입 성공", data);
        return ResponseEntity.ok(responseBody);
    }

    /**
     * 이메일 중복 검사
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestParam String email) {
        boolean isDuplicate = authService.isDuplicateEmail(email);
        if (isDuplicate) {
            return ResponseEntity.ok(new ApiResponse(false, "이미 사용 중인 이메일입니다.", null));
        }
        return ResponseEntity.ok(new ApiResponse(true, "사용 가능한 이메일입니다.", null));
    }


    /**
     * 이메일 인증 코드 요청
     */
    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse> sendVerificationCode(@RequestBody EmailRequest request) {
        authService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok(new ApiResponse(true, "인증 코드가 이메일로 전송되었습니다.", null));
    }

    /**
     * 이메일 인증 코드 검증
     */
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse> verifyEmailCode(@RequestBody EmailVerifyRequest request) {
        boolean result = authService.verifyEmailCode(request.getEmail(), request.getCode());
        if (!result) {
            return ResponseEntity.ok(new ApiResponse(false, "인증 코드가 일치하지 않습니다.", null));
        }
        return ResponseEntity.ok(new ApiResponse(true, "이메일 인증이 완료되었습니다.", null));
    }

//    // Google 로그인 URL로 redirect
//    @GetMapping("/google/login")
//    public void googleLogin(HttpServletResponse response) throws Exception {
//        String url = googleAuthService.buildGoogleLoginUrl();
//        response.sendRedirect(url);
//    }
//
//    // Google OAuth Callback
//    @GetMapping("/google/callback")
//    public ResponseEntity<?> googleCallback(
//            @RequestParam("code") String code,
//            HttpServletResponse response
//    ) {
//
//        LoginResponse data = authService.loginUser(code);
//
//        CookieUtil.addTokenCookies(response, data);
//
//        return ResponseEntity.status(HttpStatus.FOUND)
//                .location(URI.create("http://localhost:5173"))
//                .build();
//    }


    /**
     * 현재 로그인 된 유저 정보
     * SecurityContext에 있는 CustomUserDetails 추출됨
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        UserInfo userInfo = new UserInfo(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
        return ResponseEntity.ok(new ApiResponse(true, "사용자 정보 조회 성공", userInfo));
    }

    /**
     * 임시 비밀번호 발급 요청
     */
    @GetMapping("/password/reset/request")
    public ResponseEntity<ApiResponse> resetPassword()

    /**
     * 임시 비밀번호로 로그인 (이건 시큐리티에)
     */
    @GetMapping("/login/temp")


}
