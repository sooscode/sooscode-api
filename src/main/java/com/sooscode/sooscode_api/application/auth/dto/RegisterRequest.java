package com.sooscode.sooscode_api.application.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String name;
    private String password;
    private String confirmPassword;
}
