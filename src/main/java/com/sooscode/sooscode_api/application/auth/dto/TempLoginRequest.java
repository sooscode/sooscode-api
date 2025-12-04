package com.sooscode.sooscode_api.application.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TempLoginRequest {
    private String email;
    private String tempPassword;
}