package com.sooscode.sooscode_api.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.sooscode.sooscode_api.application.auth.dto.LoginRequest;
import com.sooscode.sooscode_api.application.auth.dto.LoginResponse;
import com.sooscode.sooscode_api.application.auth.dto.RegisterRequest;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.jwt.JwtUtil;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일 입니다."));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
