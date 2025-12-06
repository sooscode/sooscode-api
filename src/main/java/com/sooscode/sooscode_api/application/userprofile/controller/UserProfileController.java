package com.sooscode.sooscode_api.application.userprofile.controller;

import com.sooscode.sooscode_api.application.auth.dto.ApiResponse;
import com.sooscode.sooscode_api.application.userprofile.dto.UpdatePasswordRequest;
import com.sooscode.sooscode_api.application.userprofile.dto.UpdateProfileRequest;
import com.sooscode.sooscode_api.application.userprofile.dto.UserResponse;
import com.sooscode.sooscode_api.application.userprofile.service.UserProfileService;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

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
        userProfileService.updatePassword(userDetails.getUser(), request);

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
        User updated = userProfileService.updateProfile(user, request);

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
        userProfileService.deleteUser(user);

        return ResponseEntity.ok(
                new ApiResponse(true, "회원 탈퇴가 완료되었습니다.", null)
        );
    }

}
