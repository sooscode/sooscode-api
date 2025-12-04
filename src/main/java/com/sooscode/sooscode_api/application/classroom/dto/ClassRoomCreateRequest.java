package com.sooscode.sooscode_api.application.classroom.dto;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.enums.ClassStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
/**
 *  (Test) Create Room시 사용하는 DTO
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
