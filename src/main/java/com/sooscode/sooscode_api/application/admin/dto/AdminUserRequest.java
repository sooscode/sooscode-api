package com.sooscode.sooscode_api.application.admin.dto;

import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class AdminUserRequest {

    /**
     * 강사 계정 생성 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateInstructor {

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        private String email;

        @NotBlank(message = "이름은 필수입니다")
        @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다")
        private String name;
    }

    /**
     * 사용자 목록 조회 필터
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchFilter {

        private String keyword; // 이메일 또는 이름 검색

        private UserRole role; // 역할 필터 (INSTRUCTOR, STUDENT, ADMIN)

        private Boolean isActive; // 계정 상태 필터 (true: 활성, false: 비활성)

        private LocalDateTime startDate; // 가입일 필터 (시작)

        private LocalDateTime endDate; // 가입일 필터 (종료)

        private String sortBy = "createdAt"; // 정렬 기준: createdAt, name, email

        private String sortDirection = "DESC"; // ASC or DESC
    }

    /**
     * 역할 변경 요청
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangeRole {

        @NotNull(message = "역할은 필수입니다")
        private UserRole role; // INSTRUCTOR 또는 STUDENT만 가능 (ADMIN 불가)
    }

    /**
     * 일괄 계정 생성 요청 (CSV)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkCreate {

        @NotNull(message = "CSV 파일은 필수입니다")
        private MultipartFile csvFile;

        @NotNull(message = "역할은 필수입니다")
        private UserRole role; // 생성할 계정의 역할 (INSTRUCTOR 또는 STUDENT)
    }
}