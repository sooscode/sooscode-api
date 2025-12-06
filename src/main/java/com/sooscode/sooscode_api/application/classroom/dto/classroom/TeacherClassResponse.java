package com.sooscode.sooscode_api.application.classroom.dto.classroom;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassAssignment;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.user.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
/**
 *  Teacher 담당 Class Response DTO
 */
public class TeacherClassResponse {

    private Long classId;
    private String classTitle;
    private String classDescription;
    private LocalDateTime assignedAt;

    private Long teacherId;
    private String teacherName;

    public static TeacherClassResponse from(ClassAssignment entity) {

        User teacher = entity.getUser();
        ClassRoom classRoom = entity.getClassRoom();

        return TeacherClassResponse.builder()
                .classId(classRoom.getClassId())
                .classTitle(classRoom.getTitle())
                .classDescription(classRoom.getDescription())
                .assignedAt(entity.getCreatedAt())
                .teacherId(teacher.getUserId())
                .teacherName(teacher.getName())
                .build();
    }
}
