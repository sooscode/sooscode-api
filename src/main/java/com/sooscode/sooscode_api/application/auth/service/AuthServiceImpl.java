package com.sooscode.sooscode_api.application.auth.service;

import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.sooscode.sooscode_api.application.auth.dto.*;
import com.sooscode.sooscode_api.application.auth.dto.RegisterRequest;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.domain.user.service.UserService;
import com.sooscode.sooscode_api.global.jwt.JwtUtil;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    // 로그인
    public LoginResponse loginUser(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일 입니다."));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken
        );
    }

    // 회원가입
    public String registerUser(RegisterRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            return "이미 존재하는 이메일입니다.";
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setProvider("local");
        user.setRole(UserRole.STUDENT);
        user.setStatus(UserStatus.ACTIVE);

        User newUser = userService.saveUser(user);

        return "회원가입 완료!";
    }
}
