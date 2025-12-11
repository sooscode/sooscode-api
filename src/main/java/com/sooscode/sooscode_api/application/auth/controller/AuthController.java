package com.sooscode.sooscode_api.application.auth.controller;

import com.sooscode.sooscode_api.application.auth.service.GoogleAuthService;
import com.sooscode.sooscode_api.application.auth.util.CookieUtil;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.AuthStatus;
import com.sooscode.sooscode_api.global.api.response.ApiResponse;
import com.sooscode.sooscode_api.global.api.status.GlobalStatus;
import com.sooscode.sooscode_api.global.jwt.JwtUtil;
import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.sooscode.sooscode_api.application.auth.dto.*;
import com.sooscode.sooscode_api.application.auth.service.AuthServiceImpl;
import com.sooscode.sooscode_api.application.auth.dto.RegisterRequest;

import java.net.URI;

import static com.sooscode.sooscode_api.global.utils.UserValidator.validateSignupData;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final GoogleAuthService googleAuthService;

    /**
     * 로컬 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response)
    {
        LoginResponse userInfo = authService.authenticateAndGenerateTokens(request, authenticationManager, response);
        return ApiResponse.ok(GlobalStatus.OK, userInfo);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal CustomUserDetails user,
            @CookieValue(name = "accessToken", required = false) String accessToken,
            HttpServletResponse response
    ) {
        authService.logout(user.getUser().getUserId(), accessToken);  // RT 삭제 + AT 블랙리스트
        CookieUtil.deleteTokenCookies(response, null);
        return ApiResponse.ok(GlobalStatus.OK);
    }

    /**
     * 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody RegisterRequest request) {

        String email = request.getEmail();

        /**
         * 탈퇴한 이메일인지 먼저 검사
         */
        if (authService.isInactiveEmail(email)) {
            throw new CustomException(AuthStatus.EMAIL_INACTIVE);
        }

        /**
         * ACTIVE 상태의 중복 이메일 검사
         */
        if (authService.isDuplicateActiveEmail(email)) {
            throw new CustomException(AuthStatus.DUPLICATE_EMAIL);
        }
        /**
         * 유효성 검사 (서비스에서 호출)
         */
        validateSignupData(request.getName(), request.getEmail(), request.getPassword(), request.getConfirmPassword());

        RegisterResponse data = authService.registerUser(request);

        return ApiResponse.ok(GlobalStatus.OK, data);

    }

    /**
     * 이메일 중복 검사
     * (회원가입 이메일 입력 시 선행 체크)
     */
    @GetMapping("/email/check")
    public ResponseEntity<ApiResponse<Void>> checkEmail(@RequestParam String email) {
        if (authService.isDuplicateActiveEmail(email)) {
            throw new CustomException(AuthStatus.DUPLICATE_EMAIL);
        }

        return ApiResponse.ok(GlobalStatus.OK);
    }

    /**
     * 이메일 인증 코드 요청
     */
    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(@RequestBody EmailRequest request) {
        authService.sendVerificationCode(request.getEmail());

        return  ApiResponse.ok(GlobalStatus.OK);

    }

    /**
     * 이메일 인증 코드 검증
     */
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmailCode(@RequestBody EmailVerifyRequest request) {
        authService.verifyEmailCode(request.getEmail(), request.getCode());

        return  ApiResponse.ok(GlobalStatus.OK);
    }

    // Google 로그인 URL로 redirect
    @GetMapping("/google/login")
    public void googleLogin(HttpServletResponse response) throws Exception {
        String url = googleAuthService.buildGoogleLoginUrl();
        response.sendRedirect(url);
    }

    /**
     * Google OAuth Callback
     */
    @GetMapping("/google/callback")
    public ResponseEntity<?> googleCallback(
            @RequestParam("code") String code,
            HttpServletResponse response
    ) {

        // 소셜 로그인 후 토큰 + 유저 정보 받기
        GoogleLoginResponse data = googleAuthService.loginUserResponse(code);

        // 토큰을 쿠키에 저장
        CookieUtil.addTokenCookies(
                response,
                new TokenResponse(data.accessToken(), data.refreshToken())
        );

        // Body 없이 redirect
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:5173"))
                .build();
    }

    /**
     * RT로 AT재발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = CookieUtil.getRefreshToken(request);

        // 토큰 유효성 검사
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new CustomException(AuthStatus.REFRESH_TOKEN_EXPIRED);
        }

        TokenResponse tokens = authService.reissueAccessToken(refreshToken);

        CookieUtil.addTokenCookies(response, tokens);

        return ApiResponse.ok(GlobalStatus.OK);
    }

    /**
     * 임시 비밀번호 발급 요청
     */
//    @PostMapping("/password/reset/request")
//    public ResponseEntity<?> requestTempPassword(@RequestBody TempPasswordRequest request) {
//        authService.sendTempPassword(request.getEmail());
//        return ResponseEntity.ok(
//                new ApiResponse(true, "임시 비밀번호가 이메일로 발송되었습니다.", null)
//        );
//    }

    /**
     * 임시 비밀번호로 로그인
     */
//    @PostMapping("/login/temp")
//    public ResponseEntity<ApiResponse> tempLogin(@RequestBody TempLoginRequest request) {
//        LoginResponse tokens = authService.loginWithTempPassword(
//                request.getEmail(),
//                request.getTempPassword()
//        );
//        return ResponseEntity.ok(
//                new ApiResponse(true, "임시 비밀번호 로그인 성공", tokens)
//        );
//    }


    @GetMapping("/test")
    public ResponseEntity<?> test() {
        try {
            Thread.sleep(3000); // 3초 지연
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 인터럽트 상태 복원
        }
        System.out.println("나다");
        return ResponseEntity.ok(new SBApiResponse(true, "test", null));
    }
}