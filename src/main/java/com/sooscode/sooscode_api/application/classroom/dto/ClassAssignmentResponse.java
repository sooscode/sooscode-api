package com.sooscode.sooscode_api.application.classroom.dto;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassAssignment;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
public class ClassAssignmentResponse {
    private Long classAssignmentId;
    private Long userId;
    private Long classId;
    private LocalDateTime createdAt;

//    public static ClassAssignmentResponse from(ClassAssignment entity) {
//        return ClassAssignmentResponse.builder()
//                .classAssignmentId(entity.getClassAssignmentId())
//                .userId(entity.getUser().getUserId())
//                .classId(entity.getClassRoom().getClassId())
//                .createdAt(entity.getCreatedAt())
//                .build();
//    }
}
