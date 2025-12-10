package com.sooscode.sooscode_api.application.mypage.controller;

import com.sooscode.sooscode_api.application.auth.dto.SBApiResponse;
import com.sooscode.sooscode_api.application.mypage.dto.MypageUserUpdatePasswordRequest;
import com.sooscode.sooscode_api.application.mypage.dto.MypageUserUpdateProfileRequest;
import com.sooscode.sooscode_api.application.mypage.dto.MypageUserUpdateResponse;
import com.sooscode.sooscode_api.application.mypage.service.MypageUserService;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageUserController {

    private final MypageUserService mypageService;

    /**
     * 비밀번호 변경
     */
    @PostMapping("/password/update")
    public ResponseEntity<SBApiResponse> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MypageUserUpdatePasswordRequest request
    ) {
        mypageService.updatePassword(userDetails.getUser(), request);

        return ResponseEntity.ok(
                new SBApiResponse(true, "비밀번호가 성공적으로 변경되었습니다.", null)
        );
    }

    /**
     * 프로필 수정
     */
    @PostMapping("/profile/update")
    public ResponseEntity<SBApiResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MypageUserUpdateProfileRequest request
    ) {
        User user = userDetails.getUser();
        User updated = mypageService.updateProfile(user, request);

        MypageUserUpdateResponse response = new MypageUserUpdateResponse(
            updated.getUserId(),
            updated.getEmail(),
            updated.getName(),
            updated.getRole(),
            updated.getStatus()
        );

        return ResponseEntity.ok(
                new SBApiResponse(true, "프로필이 수정되었습니다.", response)
        );
    }

    /**
     * 회원 탈퇴
     */
    @PostMapping("/delete")
    public ResponseEntity<SBApiResponse> deleteUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        mypageService.deleteUser(user);

        return ResponseEntity.ok(
                new SBApiResponse(true, "회원 탈퇴가 완료되었습니다.", null)
        );
    }

    /**
     * 프로필 이미지 변경
     */
    @PostMapping(value = "/profile/image/update", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) throws IOException {

        Long userId = userDetails.getUser().getUserId();
        mypageService.updateProfileImage(userId, photo);

        return ResponseEntity.ok(new SBApiResponse(true, "프로필 이미지 업로드 완료", null));
    }

    /**
     * 프로필 이미지 삭제
     */
    @PostMapping("/profile/image/delete")
    public ResponseEntity<?> deleteProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Long userId = userDetails.getUser().getUserId();
        mypageService.deleteProfileImage(userId);

        return ResponseEntity.ok(new SBApiResponse(true, "프로필 이미지 삭제 완료", null));
    }
}
