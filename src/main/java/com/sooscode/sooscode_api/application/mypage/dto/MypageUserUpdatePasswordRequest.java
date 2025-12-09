package com.sooscode.sooscode_api.application.mypage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MypageUserUpdatePasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
