package com.sooscode.sooscode_api.application.admin.dto;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import com.sooscode.sooscode_api.global.ActivityType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class AdminUserResponse {

    /**
     * 계정 생성 응답
     */
    @Data
    @Builder
    public static class UserCreated {
        private Long userId;
        private String email;
        private String name;
        private String temporaryPassword; // 임시 비밀번호
        private LocalDateTime createdAt;

        public static UserCreated from(User user, String temporaryPassword) {
            return UserCreated.builder()
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
    public static class UserListItem {
        private Long userId;
        private String email;
        private String name;
        private UserRole role;
        private UserStatus isActive;
        private LocalDateTime createdAt;
        private LocalDateTime lastLoginAt;

        public static UserListItem from(User user, LocalDateTime lastLoginAt, Integer totalClassCount) {
            return UserListItem.builder()
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
     * 유저 히스토리
     */
    @Data
    @Builder
    public static class UserHistory {
        private LocalDateTime createAt;
        private ActivityType activityType;
        private String ipAddress;
        private String userAgent;
        private String note;

        public static UserHistory of(
                LocalDateTime createAt,
                ActivityType activityType,
                String ipAddress,
                String userAgent,
                String note
        ) {
            return UserHistory.builder()
                    .createAt(createAt)
                    .activityType(activityType)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .note(note)
                    .build();
        }
    }

    /**
     * 일괄 생성 결과
     */
    @Data
    @Builder
    public static class CreateResult {
        private int totalCount; // 전체 처리 건수
        private int successCount; // 성공 건수
        private int failureCount; // 실패 건수
        private List<CreateItem> details; // 상세 내역

        @Data
        @Builder
        public static class CreateItem {
            private String email;
            private String name;
            private boolean success;
            private String temporaryPassword; // 성공 시 임시 비밀번호
            private String errorMessage; // 실패 시 에러 메시지
        }
    }

    /**
     * 유저가 수강 중인 클래스 정보
     */
    @Data
    @Builder
    public static class EnrolledClass {
        private Long classId;              // 클래스 ID
        private String className;          // 클래스 이름
        private String instructorName;     // 강사 이름
        private LocalDateTime enrolledAt;  // 참가자 등록 시간

        public static EnrolledClass from(ClassParticipant participant) {
            return EnrolledClass.builder()
                    .classId(participant.getClassRoom().getClassId())
                    .className(participant.getClassRoom().getTitle())
                    .instructorName(participant.getClassRoom().getUser().getName())
                    .enrolledAt(participant.getCreatedAt())
                    .build();
        }
    }
}