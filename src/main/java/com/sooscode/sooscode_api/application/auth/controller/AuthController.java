package com.sooscode.sooscode_api.application.auth.controller;

import com.sooscode.sooscode_api.application.auth.util.CookieUtil;
import com.sooscode.sooscode_api.application.userprofile.dto.UserInfo;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.errorcode.AuthErrorCode;
import com.sooscode.sooscode_api.global.jwt.JwtUtil;
import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.sooscode.sooscode_api.application.auth.dto.*;
import com.sooscode.sooscode_api.application.auth.service.AuthServiceImpl;
import com.sooscode.sooscode_api.application.auth.dto.RegisterRequest;

import static com.sooscode.sooscode_api.global.utils.UserValidator.validateSignupData;
import static com.sooscode.sooscode_api.global.utils.UserValidator.validateUsername;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;
    private final AuthenticationManager authenticationManager;

    /**
     * 로컬 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResult result = authService.authenticateAndGenerateTokens(request, authenticationManager);

        // ✔ 쿠키 저장
        CookieUtil.addTokenCookies(response, result.getTokens());

        // ✔ 응답 body에는 유저 정보만
        return ResponseEntity.ok(
                new ApiResponse(true, "로그인 성공", new LoginResponse(result.getUser()))
        );
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @AuthenticationPrincipal CustomUserDetails user,
            HttpServletResponse response
    ) {
        if (user != null) {
            authService.deleteRefreshToken(user.getUser().getUserId());
        }

        CookieUtil.deleteTokenCookies(response, null);

        return ResponseEntity.ok(
                new ApiResponse(true, "로그아웃 성공", null)
        );
    }

    /**
     * 회원가입
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {

        /**
         * 이메일 중복 체크
         */
        if (authService.isDuplicateEmail(request.getEmail())) {
            throw new CustomException(AuthErrorCode.DUPLICATE_EMAIL);
        }
        /**
         * 유효성 검사 (서비스에서 호출)
         */
        validateSignupData(request.getName(), request.getEmail(), request.getPassword(), request.getConfirmPassword());

        RegisterResponse data = authService.registerUser(request);
        return ResponseEntity.ok(
                new ApiResponse(true, "회원가입 성공", data)
        );
    }

    /**
     * 이메일 인증 코드 요청
     */
    @PostMapping("/email/send")
    public ResponseEntity<ApiResponse> sendVerificationCode(@RequestBody EmailRequest request) {
        authService.sendVerificationCode(request.getEmail());

        return ResponseEntity.ok(
                new ApiResponse(true, "인증 코드가 이메일로 전송되었습니다.", null)
        );
    }

    /**
     * 이메일 인증 코드 검증
     */
    @PostMapping("/email/verify")
    public ResponseEntity<ApiResponse> verifyEmailCode(@RequestBody EmailVerifyRequest request) {
        boolean result = authService.verifyEmailCode(request.getEmail(), request.getCode());
        if (!result) {
            throw new CustomException(AuthErrorCode.VERIFICATION_CODE_INVALID);
        }
        return ResponseEntity.ok(
                new ApiResponse(true, "이메일 인증이 완료되었습니다.", null)
        );
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
        return ResponseEntity.ok(
                new ApiResponse(true, "사용자 정보 조회 성공", userInfo)
        );
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
    public ResponseEntity<ApiResponse> reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = CookieUtil.getRefreshToken(request);

        TokenPair tokens = authService.reissueAccessToken(refreshToken);

        CookieUtil.addTokenCookies(response, tokens);

        return ResponseEntity.ok(new ApiResponse(true, "Access Token 재발급 완료", tokens));
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        try {
            Thread.sleep(3000); // 3초 지연
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 인터럽트 상태 복원
        }
        System.out.println("나다");
        return ResponseEntity.ok(new ApiResponse(true, "test", null));
    }
}
