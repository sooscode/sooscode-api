package com.sooscode.sooscode_api.application.mypage.dto;

import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String email;
    private String name;
    private UserRole role;
    private String profileImage;
}