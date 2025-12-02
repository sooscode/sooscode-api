package com.sooscode.sooscode_api.application.auth.service;

import com.sooscode.sooscode_api.application.auth.dto.LoginRequest;
import com.sooscode.sooscode_api.application.auth.dto.LoginResponse;
import com.sooscode.sooscode_api.application.auth.dto.RegisterRequest;

public interface AuthService {
    // 로그인
    public LoginResponse loginUser(LoginRequest loginRequest);

    // 회원가입
    public String registerUser(RegisterRequest request);
}
