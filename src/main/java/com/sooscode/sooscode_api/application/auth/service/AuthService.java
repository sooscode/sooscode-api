package com.sooscode.sooscode_api.application.auth.service;

import com.sooscode.sooscode_api.application.auth.dto.*;
import com.sooscode.sooscode_api.domain.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;


public interface AuthService {
    /**
     * 이메일 + 비밀번호로 로그인하고 AT/RT + 유저 정보를 반환
     */
    LoginResponse authenticateAndGenerateTokens(LoginRequest request,
                                                AuthenticationManager authenticationManager,  HttpServletResponse response);

    /**
     * Refresh Token으로 Access Token을 재발급
     */
    TokenResponse reissueAccessToken(String refreshToken);

    /**
     * 로그아웃 - RT 삭제 + AT 블랙리스트 등록
     */
    void logout(Long userId, String accessToken);

    /**
     * 새로운 유저를 회원가입 처리하고 기본 정보 반환
     */
    RegisterResponse registerUser(RegisterRequest request);

    /**
     * 비활성화(INACTIVE) 상태의 이메일인지 여부를 확인
     */
    boolean isInactiveEmail(String email);

    /**
     * 이미 ACTIVE 상태로 가입된 이메일인지 여부를 확인
     */
    boolean isDuplicateActiveEmail(String email);

    /**
     * 회원가입 완료 전 이메일로 인증 코드를 발송
     */
    void sendVerificationCode(String email);

    /**
     * 이메일과 인증 코드를 검증해서 유효한지 확인
     */
    void verifyEmailCode(String email, String code);

    /**
     * 신규 유저 저장
     */
    User saveUser(User user);
}