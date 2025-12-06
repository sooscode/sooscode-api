package com.sooscode.sooscode_api.application.classroom.dto.assignment;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassAssignment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
/**
 *  Default ClassAssignment response DTO
 */
public class ClassAssignmentResponse {
    private Long classAssignmentId;
    private Long userId;
    private Long classId;
    private LocalDateTime createdAt;

    public static ClassAssignmentResponse from(ClassAssignment entity) {
        return ClassAssignmentResponse.builder()
                .classAssignmentId(entity.getClassAssignmentId())
                .userId(entity.getUser().getUserId())
                .classId(entity.getClassRoom().getClassId())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
