package com.sooscode.sooscode_api.application.admin.dto;

import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
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
    public static class Create {
        private String email;
        private String name;
        private String role;
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
        private UserStatus status; // 계정 상태 필터 (true: 활성, false: 비활성)
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
        private UserRole role;
    }

    /**
     * 일괄 계정 생성 요청 (CSV)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkCreate {
        private MultipartFile csvFile;
        private UserRole role;
    }
}