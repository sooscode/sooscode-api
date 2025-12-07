package com.sooscode.sooscode_api.application.mypage.controller;

import com.sooscode.sooscode_api.application.auth.dto.ApiResponse;
import com.sooscode.sooscode_api.application.classroom.service.TestServiceImpl;
import com.sooscode.sooscode_api.application.mypage.dto.UpdatePasswordRequest;
import com.sooscode.sooscode_api.application.mypage.dto.UpdateProfileRequest;
import com.sooscode.sooscode_api.application.mypage.dto.UserResponse;
import com.sooscode.sooscode_api.application.mypage.service.MypageService;
import com.sooscode.sooscode_api.application.mypage.service.MypageServiceImpl;
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
public class MypageController {

    private final MypageService mypageService;

    /**
     * 프로필 조회
     * */
    @GetMapping("/profile")
    public UserResponse getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        return new UserResponse(
            user.getUserId(),
            user.getEmail(),
            user.getName(),
            user.getRole(),
            user.getStatus()
        );
    }


    /**
     * 비밀번호 변경
     */
    @PostMapping("/password/update")
    public ResponseEntity<ApiResponse> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdatePasswordRequest request
    ) {
        mypageService.updatePassword(userDetails.getUser(), request);

        return ResponseEntity.ok(
                new ApiResponse(true, "비밀번호가 성공적으로 변경되었습니다.", null)
        );
    }

    /**
     * 프로필 수정
     */
    @PostMapping("/profile/update")
    public ResponseEntity<ApiResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateProfileRequest request
    ) {
        User user = userDetails.getUser();
        User updated = mypageService.updateProfile(user, request);

        UserResponse response = new UserResponse(
            updated.getUserId(),
            updated.getEmail(),
            updated.getName(),
            updated.getRole(),
            updated.getStatus()
        );

        return ResponseEntity.ok(
                new ApiResponse(true, "프로필이 수정되었습니다.", response)
        );
    }

    /**
     * 회원 탈퇴
     */
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse> deleteUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        mypageService.deleteUser(user);

        return ResponseEntity.ok(
                new ApiResponse(true, "회원 탈퇴가 완료되었습니다.", null)
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

        return ResponseEntity.ok(new ApiResponse(true, "프로필 이미지 업로드 완료", null));
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

        return ResponseEntity.ok(new ApiResponse(true, "프로필 이미지 삭제 완료", null));
    }


    /**
     * 내가 참여하고 있는 클래스 조회
     */
    @GetMapping("/classes")
    public ResponseEntity<ApiResponse> getMyClasses(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();

        return ResponseEntity.ok(
                new ApiResponse(true, "내 클래스 목록 조회 성공",
                        mypageService.getMyClasses(user))
        );
    }

}
