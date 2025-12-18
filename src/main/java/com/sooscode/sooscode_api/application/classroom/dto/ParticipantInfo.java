package com.sooscode.sooscode_api.application.classroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 참여자 정보 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantInfo {

    private Long userId;
    private String username;
    private boolean instructor;
    private LocalDateTime joinedAt;
}