package com.sooscode.sooscode_api.application.mypage.dto;

import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String email;
    private String name;
    private UserRole role;
    private UserStatus status;
}