package com.sooscode.sooscode_api.application.admin.dto;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.user.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AdminClassResponse {

    /**
     * 클래스 목록 아이템
     */
    @Data
    @Builder
    public static class ClassItem {
        private Long classId;
        private String thumbnail;
        private String title;
        private String description;
        private boolean isOnline;
        private boolean isActive;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private String instructorName;
        private Integer studentCount;

        public static ClassItem from(ClassRoom classRoom, String thumbnail, String instructorName, Integer studentCount) {
            return ClassItem.builder()
                    .classId(classRoom.getClassId())
                    .thumbnail(thumbnail)
                    .title(classRoom.getTitle())
                    .description(classRoom.getDescription())
                    .isOnline(classRoom.isOnline())
                    .isActive(classRoom.isActive())
                    .startDate(classRoom.getStartDate())
                    .endDate(classRoom.getEndDate())
                    .startTime(classRoom.getStartTime())
                    .endTime(classRoom.getEndTime())
                    .instructorName(instructorName)
                    .studentCount(studentCount)
                    .build();
        }
    }

    /**
     * 학생 배정/삭제 결과 (개별)
     */
    @Data
    @Builder
    public static class StudentOperationResult {
        private Long studentId;
        private String studentName;
        private boolean success;
        private String message;  // 성공/실패 사유
    }

    /**
     * 학생 일괄 배정/삭제 응답
     */
    @Data
    @Builder
    public static class StudentOperationResponse {
        private int totalCount;        // 전체 요청 학생 수
        private int successCount;      // 성공한 학생 수
        private int failureCount;      // 실패한 학생 수
        private List<StudentOperationResult> results;  // 각 학생별 결과
    }

    /**
     * 클래스 학생 정보
     */
    @Data
    @Builder
    public static class ClassStudentsResponse {
        private Long userId;
        private String name;
        private String email;
        private String profileImage;
        private LocalDateTime enrolledAt;  // 등록일시

        public static ClassStudentsResponse from(ClassParticipant participant) {
            User user = participant.getUser();
            return ClassStudentsResponse.builder()
                    .userId(user.getUserId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .profileImage(user.getProfileImage())
                    .enrolledAt(participant.getCreatedAt())
                    .build();
        }
    }
}