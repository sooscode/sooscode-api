package com.sooscode.sooscode_api.application.auth.service;

import com.sooscode.sooscode_api.application.auth.dto.*;
import org.springframework.security.authentication.AuthenticationManager;


public interface AuthService {
    /**
     * 이메일 + 비밀번호로 로그인하고 AT/RT + 유저 정보를 반환
     */
    LoginResult authenticateAndGenerateTokens(LoginRequest request,
                                              AuthenticationManager authenticationManager);

    /**
     * Refresh Token으로 Access Token을 재발급
     */
    TokenPair reissueAccessToken(String refreshToken);

    /**
     * 특정 유저의 Refresh Token을 삭제
     */
    void deleteRefreshToken(Long userId);

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
    boolean verifyEmailCode(String email, String code);
}
