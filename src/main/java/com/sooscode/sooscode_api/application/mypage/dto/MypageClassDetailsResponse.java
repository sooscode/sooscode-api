package com.sooscode.sooscode_api.application.mypage.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
/**
 *  Teacher 담당 Class Response DTO
 */
public class MypageClassDetailsResponse {

    private Long classId;
    private String classTitle;
    private String classDescription;
    private LocalDateTime assignedAt;
    private Long teacherId;
    private String teacherName;
}
