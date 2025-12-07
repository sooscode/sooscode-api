package com.sooscode.sooscode_api.application.auth.dto;

import com.sooscode.sooscode_api.application.mypage.dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private UserInfo user;
}