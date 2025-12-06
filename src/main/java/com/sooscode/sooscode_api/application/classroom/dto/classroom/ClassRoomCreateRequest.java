package com.sooscode.sooscode_api.application.classroom.dto.classroom;

import com.sooscode.sooscode_api.domain.classroom.enums.ClassStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
/**
 *  Room Create Request
 */
public class ClassRoomCreateRequest {
    private boolean isOnline;
    private String title;
    private String description;
    private Long fileId;
    private ClassStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
