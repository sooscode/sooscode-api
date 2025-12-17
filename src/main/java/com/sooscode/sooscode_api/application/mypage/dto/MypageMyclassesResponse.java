package com.sooscode.sooscode_api.application.mypage.dto;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor

public class MypageMyclassesResponse {
        private Long classId;
        private String title;
        private String teacherName;
        private String thumbnailUrl;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalTime startTime;
        private LocalTime endTime;

    public static MypageMyclassesResponse from(
            ClassRoom classRoom,
            String thumbnailUrl
    ) {
        return MypageMyclassesResponse.builder()
                .classId(classRoom.getClassId())
                .title(classRoom.getTitle())
                .teacherName(classRoom.getUser().getName())
                .thumbnailUrl(thumbnailUrl)
                .startDate(classRoom.getStartDate())
                .endDate(classRoom.getEndDate())
                .startTime(classRoom.getStartTime())
                .endTime(classRoom.getEndTime())
                .build();
    }

}
