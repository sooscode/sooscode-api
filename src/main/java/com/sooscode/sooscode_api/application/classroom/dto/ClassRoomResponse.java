package com.sooscode.sooscode_api.application.classroom.dto;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ClassRoomResponse {

    private Long classId;
    private boolean online;
    private String title;
    private String description;
    private String status;
    private String mode;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public static ClassRoomResponse from(ClassRoom classRoom) {
        return ClassRoomResponse.builder()
                .classId(classRoom.getClassId())
                .online(classRoom.isOnline())
                .title(classRoom.getTitle())
                .description(classRoom.getDescription())
                .status(classRoom.getStatus() != null ? classRoom.getStatus().name() : null)
                .mode(classRoom.getMode().name())
                .startedAt(classRoom.getStartedAt())
                .endedAt(classRoom.getEndedAt())
                .build();
    }
}