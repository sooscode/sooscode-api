package com.sooscode.sooscode_api.application.classroom.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantInfo {
    private Long userId;
    private String username;
    private String userEmail;
    private String role;        // INSTRUCTOR / STUDENT
    private boolean isOnline;
}