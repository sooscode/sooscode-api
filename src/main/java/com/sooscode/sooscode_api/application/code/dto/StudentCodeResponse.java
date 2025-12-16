package com.sooscode.sooscode_api.application.code.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentCodeResponse {
    private Long userId;
    private String username;
    private String code;
    private String language;
    private Long classId;
}