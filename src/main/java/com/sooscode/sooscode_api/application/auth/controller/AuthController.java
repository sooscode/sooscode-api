package com.sooscode.sooscode_api.application.auth.controller;

import com.sooscode.sooscode_api.application.auth.util.CookieUtil;
import com.sooscode.sooscode_api.domain.user.entity.EmailCode;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.global.user.CustomUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<ApiResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResponse data = authService.loginUser(request);

        // 쿠키 굽기
        CookieUtil.addTokenCookies(response, data);

        // JSON body 응답
        ApiResponse responseBody = new ApiResponse(true, "로그인 성공", data);

        return ResponseEntity.ok(responseBody);
    }


    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletResponse response) {

       CookieUtil.deleteTokenCookies(response, null);

       ApiResponse responseBody = new ApiResponse(true, "로그아웃 성공", null);

        return ResponseEntity.ok(responseBody);
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {

        RegisterResponse data = authService.registerUser(request);

        ApiResponse responseBody = new ApiResponse(true, "회원가입 성공", data);

        return ResponseEntity.ok(responseBody);
    }

    // 이메일 중복 검사
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestParam String email) {

        boolean isDuplicate = authService.isDuplicateEmail(email);

        if (isDuplicate) {
            return ResponseEntity.ok(new ApiResponse(false, "이미 사용 중인 이메일입니다.", null));
        }

        return ResponseEntity.ok(new ApiResponse(true, "사용 가능한 이메일입니다.", null));
    }

    // 이메일 인증 코드 요청
    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse> sendVerificationCode(@RequestBody EmailRequest request) {
        authService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok(new ApiResponse(true, "인증 코드가 이메일로 전송되었습니다.", null));
    }

    // 이메일 인증 코드 검증
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


    // 현재 로그인된 유저 정보 테스트
    @GetMapping("/me")
    public String me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return user.getEmail();
    }
}
