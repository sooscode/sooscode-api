package com.sooscode.sooscode_api.application.mypage.dto;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
/**
 *  Class Info 전달 DTO
 */
public class ClassRoomInfoResponse {
    private Long classId;
    private String title;
    private String description;
    private boolean isOnline;
    private String mode;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public static ClassRoomInfoResponse from(ClassRoom classRoom){
        return ClassRoomInfoResponse.builder()
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
