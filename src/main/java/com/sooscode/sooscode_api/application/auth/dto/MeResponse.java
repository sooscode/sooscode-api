package com.sooscode.sooscode_api.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeResponse {
    private Long userId;
    private String email;
    private String name;
    private String role;
    private String profileImage;
}