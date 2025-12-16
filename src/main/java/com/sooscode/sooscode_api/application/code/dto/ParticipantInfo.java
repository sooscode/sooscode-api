package com.sooscode.sooscode_api.application.code.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantInfo {
    private Long userId;
    private String username;
    private String role;     // INSTRUCTOR / STUDENT
    private boolean isOnline;
}