package com.sooscode.sooscode_api.application.userprofile.controller;

import com.sooscode.sooscode_api.application.auth.dto.ApiResponse;
import com.sooscode.sooscode_api.application.userprofile.dto.UpdatePasswordRequest;
import com.sooscode.sooscode_api.application.userprofile.service.UserProfileService;
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

    @GetMapping("/profile")
    public com.sooscode.sooscode_api.application.userprofile.dto.UserResponse getProfile(
            @AuthenticationPrincipal UserDetails user
    ) {
        return new com.sooscode.sooscode_api.application.userprofile.dto.UserResponse(user.getUsername());
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

}
