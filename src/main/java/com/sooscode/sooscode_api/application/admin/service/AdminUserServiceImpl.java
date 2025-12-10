package com.sooscode.sooscode_api.application.admin.service;

import com.sooscode.sooscode_api.application.admin.dto.AdminUserRequest;
import com.sooscode.sooscode_api.application.admin.dto.AdminUserResponse;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.AuthProvider;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.application.admin.status.AuthStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public AdminUserResponse.InstructorCreated createInstructor(AdminUserRequest.CreateInstructor request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(AuthStatus.DUPLICATE_EMAIL);
        }

        // 임시 비밀번호 생성 (8자리 랜덤)
        String temporaryPassword = generateTemporaryPassword();

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(temporaryPassword);

        // User 엔티티 생성
        User instructor = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .provider(AuthProvider.LOCAL) // 관리자가 생성한 계정은 로컬 계정
                .role(UserRole.INSTRUCTOR)
                .status(UserStatus.ACTIVE) // 생성 시 활성 상태
                .build();

        // DB 저장
        User saved = userRepository.save(instructor);
        log.info("강사 계정 생성 완료: userId={}, email={}", saved.getUserId(), saved.getEmail());

        // TODO: 이메일 전송
        // emailService.sendInstructorCredentials(saved.getEmail(), saved.getName(), temporaryPassword);

        // 응답 DTO 생성
        return AdminUserResponse.InstructorCreated.from(saved, temporaryPassword);
    }



    @Override
    public AdminUserResponse.PageResponse getUserList(AdminUserRequest.SearchFilter filter, int page, int size) {
        return null;
    }

    @Override
    public AdminUserResponse.Detail getUserDetail(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return AdminUserResponse.Detail.from(user);
    }

    @Override
    public List<AdminUserResponse.LoginHistory> getLoginHistory(Long userId, int limit) {
        return List.of();
    }

    @Override
    public void deleteUser(Long userId) {

    }

    @Override
    public void toggleUserStatus(Long userId, boolean isActive) {

    }

    @Override
    public void changeUserRole(Long userId, AdminUserRequest.ChangeRole request) {

    }

    @Override
    public AdminUserResponse.BulkCreateResult bulkCreateUsers(AdminUserRequest.BulkCreate request) {
        return null;
    }

    @Override
    public byte[] exportUsersToExcel(AdminUserRequest.SearchFilter filter) {
        return new byte[0];
    }

    @Override
    public AdminUserResponse.Statistics getUserStatistics() {
        return null;
    }

    // ===== 내부 헬퍼 메서드 =====

    /**
     * 임시 비밀번호 생성 (8자리: 대문자, 소문자, 숫자 조합)
     */
    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }
}
