package com.sooscode.sooscode_api.application.classroom.dto;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClassInfoResponse {
    private Long classId;
    private String title;
    private String description;
    private boolean isOnline;
    private String mode;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public static ClassInfoResponse from(ClassRoom classRoom){
        return ClassInfoResponse.builder()
                .classId(classRoom.getClassId())
                .title(classRoom.getTitle())
                .description(classRoom.getDescription())
                .isOnline(classRoom.isOnline())
                .mode(classRoom.getMode().name())
                .status(classRoom.getStatus().name())
                .startedAt(classRoom.getStartedAt())
                .endedAt(classRoom.getEndedAt())
                .build();
    }
}
