package com.sooscode.sooscode_api.application.userprofile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    @GetMapping("/profile")
    public com.sooscode.sooscode_api.application.userprofile.dto.UserResponse getProfile(
            @AuthenticationPrincipal UserDetails user
    ) {
        return new com.sooscode.sooscode_api.application.userprofile.dto.UserResponse(user.getUsername());
    }

    /**
     * 비밀번호 변경
     */
   // @PostMapping("/password/update")

}
