package com.sooscode.sooscode_api.application.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class MypageClassResponse {
    private Long classId;
    private String title;
    private List<String> teacherName;
    private LocalDateTime joinedAt;
}
