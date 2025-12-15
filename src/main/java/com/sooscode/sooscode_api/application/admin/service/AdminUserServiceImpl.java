package com.sooscode.sooscode_api.application.admin.service;

import com.sooscode.sooscode_api.application.admin.dto.AdminPageResponse;
import com.sooscode.sooscode_api.application.admin.dto.AdminUserRequest;
import com.sooscode.sooscode_api.application.admin.dto.AdminUserResponse;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.AuthProvider;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.AuthStatus;
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


    //유저 생성
    @Override
    @Transactional
    public AdminUserResponse.UserCreated createUser(AdminUserRequest.Create request, UserRole role) {
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
                .role(role)
                .status(UserStatus.ACTIVE) // 생성 시 활성 상태
                .build();

        // DB 저장
        User saved = userRepository.save(instructor);
        log.info("강사 계정 생성 완료: userId={}, email={}", saved.getUserId(), saved.getEmail());

        // TODO: 이메일 전송
        // emailService.sendInstructorCredentials(saved.getEmail(), saved.getName(), temporaryPassword);

        // 응답 DTO 생성
        return AdminUserResponse.UserCreated.from(saved, temporaryPassword);
    }

    @Override
    public AdminPageResponse<AdminUserResponse.UserListItem> getUserList(
            AdminUserRequest.SearchFilter filter,
            int page,
            int size
    ) {
        return null;
    }

    @Override
    public AdminUserResponse.Detail getUserDetail(Long userId) {return null;}

    /**
     * 사용자 활동 히스토리 조회
     * 최근 N회의 활동 일시 및 IP 주소, 브라우저를 조회
     *
     * @param userId 조회할 사용자 ID
     * @param limit 조회할 개수 (기본 5개)
     * @return 유저 활동 히스토리 목록
     * @throws CustomException USER_NOT_FOUND - 사용자를 찾을 수 없는 경우
     */
    @Override
    public List<AdminUserResponse.UserHistory> getUserHistory(Long userId, int limit) {return null;}

    @Override
    public void deleteUser(Long userId) {}

    /**
     * 사용자 계정 활성화
     * 사용자의 계정 상태(isActive)를 활성화
     *
     * @param userId 사용자 ID
     * @throws CustomException USER_NOT_FOUND - 사용자를 찾을 수 없는 경우
     * @throws CustomException FORBIDDEN - 관리자 계정을 활성화하려는 경우
     */
    @Override
    public void activeUser(Long userId) {}

    /**
     * 사용자 역할 변경
     * 사용자의 역할(강사/학생)을 변경
     * 관리자 역할로는 변경할 수 없음
     *
     * @param userId 사용자 ID
     * @param request 역할 변경 요청 DTO
     * @throws CustomException USER_NOT_FOUND - 사용자를 찾을 수 없는 경우
     * @throws CustomException FORBIDDEN - 관리자 역할로 변경하려는 경우
     */
    @Override
    public void changeUserRole(Long userId, AdminUserRequest.ChangeRole request) {}

    /**
     * 일괄 계정 생성 (CSV)
     * CSV 파일을 업로드하여 여러 계정을 동시에 생성합니다.
     * 각 계정에 대해 임시 비밀번호가 자동 발급됩니다.
     *
     * @param request CSV 파일 업로드 요청
     * @return 생성 결과 CSV (성공/실패 건수 및 상세 내역)
     * @throws CustomException FILE_TYPE_NOT_ALLOWED - CSV 파일이 아닌 경우
     * @throws CustomException BAD_REQUEST - CSV 형식이 올바르지 않은 경우
     */
    @Override
    public AdminUserResponse.CreateResult bulkCreateUsers(AdminUserRequest.BulkCreate request) {return null;}

    /**
     * 사용자 데이터 엑셀 다운로드
     * 전체 또는 필터링된 사용자 목록을 엑셀 파일로 생성합니다.
     *
     * @param filter 검색 및 필터 조건
     * @return 엑셀 파일 바이트 배열
     */
    @Override
    public byte[] exportUsersToExcel(AdminUserRequest.SearchFilter filter) {return null;}

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
