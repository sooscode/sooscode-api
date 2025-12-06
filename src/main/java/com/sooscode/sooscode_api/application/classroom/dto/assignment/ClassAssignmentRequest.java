package com.sooscode.sooscode_api.application.classroom.dto.assignment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
/**
 *  Default ClassAssignment request DTO
 */
public class ClassAssignmentRequest {
    private Long classAssignmentId;
    private Long userId;
    private Long classId;
    private LocalDateTime createdAt;
}