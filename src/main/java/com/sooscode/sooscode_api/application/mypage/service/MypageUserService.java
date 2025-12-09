package com.sooscode.sooscode_api.application.mypage.service;

import com.sooscode.sooscode_api.application.mypage.dto.MypageUserUpdatePasswordRequest;
import com.sooscode.sooscode_api.application.mypage.dto.MypageUserUpdateProfileRequest;
import com.sooscode.sooscode_api.domain.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MypageUserService {
    /**
     * 비밀번호 변경
     */
    void updatePassword(User user, MypageUserUpdatePasswordRequest request);

    /**
     *
     * 내 이름 변경
     */
    User updateProfile(User user, MypageUserUpdateProfileRequest request);

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

}
