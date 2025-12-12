package com.sooscode.sooscode_api.application.admin.dto;

import com.sooscode.sooscode_api.domain.classroom.enums.ClassStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AdminClassRequest {

    /**
     * 클래스 생성 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private String title;
        private String description;
        private Long instructorId;
        private Boolean isOnline;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalTime startTime;
        private LocalTime endTime;
    }

    /**
     * 클래스 수정 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        private String title;
        private String description;
        private Long instructorId;
        private Boolean isOnline;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalTime startTime;
        private LocalTime endTime;
    }

    /**
     * 강사 배정 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignInstructor {
        private Long instructorId;
    }

    /**
     * 학생 배정 요청 (일괄)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Students {
        private List<Long> studentIds;
    }

    /**
     * 클래스 목록 조회 필터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchFilter {
        private String keyword; // 제목 또는 강사명 검색
        private ClassStatus status; // UPCOMING, ONGOING, FINISHED
        private LocalDate startDate; // 기간 필터 (시작)
        private LocalDate endDate; // 기간 필터 (종료)
        private String sortBy = "createdAt"; // 정렬 기준: createdAt, participantCount, duration
        private String sortDirection = "DESC"; // ASC or DESC
    }
}