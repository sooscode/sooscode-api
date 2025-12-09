package com.sooscode.sooscode_api.application.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
    private boolean rememberMe;
}