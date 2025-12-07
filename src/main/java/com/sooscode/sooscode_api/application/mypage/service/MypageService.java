package com.sooscode.sooscode_api.application.mypage.service;

import com.sooscode.sooscode_api.application.mypage.dto.MyClassResponse;
import com.sooscode.sooscode_api.application.mypage.dto.UpdatePasswordRequest;
import com.sooscode.sooscode_api.application.mypage.dto.UpdateProfileRequest;
import com.sooscode.sooscode_api.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MypageService {
    /**
     * 비밀번호 변경
     */
    void updatePassword(User user, UpdatePasswordRequest request);

    /**
     *
     * 내 이름 변경
     */
    User updateProfile(User user, UpdateProfileRequest request);

    /**
     * 회원 탈퇴 처리 (상태 INACTIVE 전환)
     */
    void deleteUser(User user);

    /**
     * 프로필 이미지 업로드
     */
    void updateProfileImage(Long userId, MultipartFile photo) throws IOException;

    /**
     * 프로필 이미지 삭제
     */
    void deleteProfileImage(Long userId);

   // String getProfileImage(Long userId);

    /**
     * 내가 참여하고 있는 클래스 조회
     */
    List<MyClassResponse> getMyClasses(User user);
}
