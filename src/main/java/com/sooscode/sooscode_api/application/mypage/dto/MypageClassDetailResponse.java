package com.sooscode.sooscode_api.application.mypage.dto;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
public class MypageClassDetailResponse {

    private Long classId;
    private String title;
    private String description;
    private String mode;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public static MypageClassDetailResponse from(ClassRoom classRoom) {
        return MypageClassDetailResponse.builder()
                .classId(classRoom.getClassId())
                .title(classRoom.getTitle())
                .description(classRoom.getDescription())
                .mode(classRoom.getMode().name())
                .status(classRoom.getStatus().name())
                .startDate(classRoom.getStartDate())
                .endDate(classRoom.getEndDate())
                .startTime(classRoom.getStartTime())
                .endTime(classRoom.getEndTime())
                .build();
    }
}

