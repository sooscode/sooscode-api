package com.sooscode.sooscode_api.application.userprofile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.sooscode.sooscode_api.application.userprofile.dto.UserProfileResponse;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {

    @GetMapping("/profile")
    public UserProfileResponse getProfile(
            @AuthenticationPrincipal UserDetails user
    ) {
        return new UserProfileResponse(user.getUsername());
    }
}
