package com.sooscode.sooscode_api.application.admin.dto;

import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class AdminUserResponse {

    /**
     * 강사 계정 생성 응답
     */
    @Data
    @Builder
    public static class InstructorCreated {
        private Long userId;
        private String email;
        private String name;
        private String temporaryPassword; // 임시 비밀번호
        private LocalDateTime createdAt;

        public static InstructorCreated from(User user, String temporaryPassword) {
            return InstructorCreated.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .temporaryPassword(temporaryPassword)
                    .createdAt(user.getCreatedAt())
                    .build();
        }
    }

    /**
     * 사용자 목록 아이템
     */
    @Data
    @Builder
    public static class ListItem {
        private Long userId;
        private String email;
        private String name;
        private UserRole role;
        private UserStatus isActive;
        private LocalDateTime createdAt;
        private LocalDateTime lastLoginAt;

        public static ListItem from(User user, LocalDateTime lastLoginAt, Integer totalClassCount) {
            return ListItem.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole())
                    .isActive(user.getStatus())
                    .createdAt(user.getCreatedAt())
                    .lastLoginAt(lastLoginAt)
                    .build();
        }
    }

    /**
     * 사용자 상세 정보
     */
    @Data
    @Builder
    public static class Detail {
        private Long userId;
        private String email;
        private String name;
        private UserRole role;
        private UserStatus isActive;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Detail from(User user) {
            return Detail.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole())
                    .isActive(user.getStatus())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
        }
    }

    /**
     * 로그인 히스토리
     */
    @Data
    @Builder
    public static class LoginHistory {
        private LocalDateTime loginAt;
        private String ipAddress;
        private String userAgent;

        public static LoginHistory of(LocalDateTime loginAt, String ipAddress, String userAgent) {
            return LoginHistory.builder()
                    .loginAt(loginAt)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();
        }
    }

    /**
     * 일괄 생성 결과
     */
    @Data
    @Builder
    public static class BulkCreateResult {
        private int totalCount; // 전체 처리 건수
        private int successCount; // 성공 건수
        private int failureCount; // 실패 건수
        private List<BulkCreateItem> details; // 상세 내역

        @Data
        @Builder
        public static class BulkCreateItem {
            private String email;
            private String name;
            private boolean success;
            private String temporaryPassword; // 성공 시 임시 비밀번호
            private String errorMessage; // 실패 시 에러 메시지
        }
    }

    /**
     * 사용자 통계
     */
    @Data
    @Builder
    public static class Statistics {
        private long totalUsers; // 전체 사용자 수
        private long activeUsers; // 활성 사용자 수
        private long inactiveUsers; // 비활성 사용자 수
        private long instructorCount; // 강사 수
        private long studentCount; // 학생 수
        private long adminCount; // 관리자 수
        private long newUsersThisWeek; // 최근 7일 신규 가입자
        private long newUsersThisMonth; // 최근 30일 신규 가입자

        public static Statistics of(long totalUsers, long activeUsers, long inactiveUsers,
                                    long instructorCount, long studentCount, long adminCount,
                                    long newUsersThisWeek, long newUsersThisMonth) {
            return Statistics.builder()
                    .totalUsers(totalUsers)
                    .activeUsers(activeUsers)
                    .inactiveUsers(inactiveUsers)
                    .instructorCount(instructorCount)
                    .studentCount(studentCount)
                    .adminCount(adminCount)
                    .newUsersThisWeek(newUsersThisWeek)
                    .newUsersThisMonth(newUsersThisMonth)
                    .build();
        }
    }
}