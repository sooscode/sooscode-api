package com.sooscode.sooscode_api.application.userprofile.service;

import com.sooscode.sooscode_api.application.userprofile.dto.UpdatePasswordRequest;
import com.sooscode.sooscode_api.application.userprofile.dto.UpdateProfileRequest;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void updatePassword(User user, UpdatePasswordRequest request) {

        // 현재 비밀번호 비교
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 암호화해서 저장
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    @Override
    public User updateProfile(User user, UpdateProfileRequest request) {

        if (request.getName() != null) {
            user.setName(request.getName());
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }
}
