package com.sooscode.sooscode_api.application.auth.controller;

import com.sooscode.sooscode_api.application.auth.service.GoogleAuthService;
import com.sooscode.sooscode_api.application.auth.util.CookieUtil;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.application.admin.status.AuthStatus;
import com.sooscode.sooscode_api.global.api.response.ApiResponse;
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

        return ApiResponse.ok(AuthStatus.OK, userInfo);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<SBApiResponse> logout(
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletResponse response
    ) {
        if (user != null) {
            authService.deleteRefreshToken(user.getUser().getUserId());
        }

        CookieUtil.deleteTokenCookies(response, null);

        return ResponseEntity.ok(
                new SBApiResponse(true, "로그아웃 성공", null)
        );
    }

    /**
     * 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<SBApiResponse> register(@RequestBody RegisterRequest request) {

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
        return ResponseEntity.ok(
                new SBApiResponse(true, "회원가입 성공", data)
        );
    }

    /**
     * 이메일 인증 코드 요청
     */
    @PostMapping("/email/send")
    public ResponseEntity<SBApiResponse> sendVerificationCode(@RequestBody EmailRequest request) {
        authService.sendVerificationCode(request.getEmail());

        return ResponseEntity.ok(
                new SBApiResponse(true, "인증 코드가 이메일로 전송되었습니다.", null)
        );
    }

    /**
     * 이메일 인증 코드 검증
     */
    @PostMapping("/email/verify")
    public ResponseEntity<SBApiResponse> verifyEmailCode(@RequestBody EmailVerifyRequest request) {
        boolean result = authService.verifyEmailCode(request.getEmail(), request.getCode());
        if (!result) {
            throw new CustomException(AuthStatus.VERIFICATION_CODE_INVALID);
        }
        return ResponseEntity.ok(
                new SBApiResponse(true, "이메일 인증이 완료되었습니다.", null)
        );
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
        GoogleLoginResponse data = authService.loginUserResponse(code);

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
     * 현재 로그인 된 유저 정보
     * SecurityContext에 있는 CustomUserDetails 추출됨
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponse>> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        MeResponse meResponse = new MeResponse(
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                user.getProfileImage()
        );
       return ApiResponse.ok(meResponse);
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

    /**
     * RT로 AT재발급
     */
    @PostMapping("/token/reissue")
    public ResponseEntity<SBApiResponse> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = CookieUtil.getRefreshToken(request);

        TokenResponse tokens = authService.reissueAccessToken(refreshToken);

        CookieUtil.addTokenCookies(response, tokens);

        return ResponseEntity.ok(new SBApiResponse(true, "Access Token 재발급 완료", tokens));
    }

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