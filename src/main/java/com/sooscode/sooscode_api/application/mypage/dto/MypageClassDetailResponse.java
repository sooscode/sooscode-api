package com.sooscode.sooscode_api.application.mypage.dto;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MypageClassDetailResponse {

    private Long classId;
    private String title;
    private String description;
    private String mode;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public static MypageClassDetailResponse from(ClassRoom classRoom) {
        return MypageClassDetailResponse.builder()
                .classId(classRoom.getClassId())
                .title(classRoom.getTitle())
                .description(classRoom.getDescription())
                .mode(classRoom.getMode().name())
                .status(classRoom.getStatus().name())
                .startedAt(classRoom.getStartedAt())
                .endedAt(classRoom.getEndedAt())
                .build();
    }
}

