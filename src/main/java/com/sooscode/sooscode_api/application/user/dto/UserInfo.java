package com.sooscode.sooscode_api.application.user.dto;

import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private Long userId;
    private String email;
    private String name;
    private UserRole role;
}