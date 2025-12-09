package com.sooscode.sooscode_api.application.admin.dto;

import com.sooscode.sooscode_api.domain.classroom.enums.ClassMode;
import com.sooscode.sooscode_api.domain.classroom.enums.ClassStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
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
        private Boolean isOnline;
        private LocalDateTime startedAt;
        private LocalDateTime endedAt;
    }

    /**
     * 클래스 수정 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {

        @NotBlank(message = "클래스 제목은 필수입니다")
        @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다")
        private String title;

        @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다")
        private String description;

        @NotNull(message = "시작 일시는 필수입니다")
        private LocalDateTime startedAt;

        @NotNull(message = "종료 일시는 필수입니다")
        private LocalDateTime endedAt;
    }

    /**
     * 강사 배정 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignInstructor {

        @NotNull(message = "강사 ID는 필수입니다")
        private Long instructorId;
    }

    /**
     * 학생 배정 요청 (일괄)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignStudents {

        @NotEmpty(message = "최소 1명 이상의 학생을 선택해야 합니다")
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

        private LocalDateTime startDate; // 기간 필터 (시작)

        private LocalDateTime endDate; // 기간 필터 (종료)

        private String sortBy = "createdAt"; // 정렬 기준: createdAt, participantCount, duration

        private String sortDirection = "DESC"; // ASC or DESC
    }
}