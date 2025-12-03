package com.sooscode.sooscode_api.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TestResponse {
    private String email;
    private String role;
}