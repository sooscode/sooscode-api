package com.sooscode.sooscode_api.application.admin.dto;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.enums.ClassStatus;
import lombok.*;

import java.time.LocalDateTime;
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
        private ClassStatus status;
        private boolean isActive;
        private LocalDateTime startedAt;
        private LocalDateTime endedAt;
        private String instructorName;
        private Integer studentCount;

        public static ClassItem from(ClassRoom classRoom, String thumbnail, String instructorName, Integer studentCount) {
            return ClassItem.builder()
                    .classId(classRoom.getClassId())
                    .thumbnail(thumbnail)
                    .title(classRoom.getTitle())
                    .description(classRoom.getDescription())
                    .isOnline(classRoom.isOnline())
                    .status(classRoom.getStatus())
                    .isActive(classRoom.isActive())
                    .startedAt(classRoom.getStartedAt())
                    .endedAt(classRoom.getEndedAt())
                    .instructorName(instructorName)
                    .studentCount(studentCount)
                    .build();
        }
    }

    /**
     * 클래스 상세 정보
     */
    @Data
    @Builder
    public static class Detail {
        private Long classId;
        private String thumbnail;
        private String title;
        private String description;
        private boolean isOnline;
        private ClassStatus status;
        private boolean isActive;
        private LocalDateTime startedAt;
        private LocalDateTime endedAt;
        private String instructorName;
        private Integer studentCount;

        public static Detail from(ClassRoom classRoom, String thumbnail, String instructorName, Integer studentCount) {
            return Detail.builder()
                    .classId(classRoom.getClassId())
                    .thumbnail(thumbnail)
                    .title(classRoom.getTitle())
                    .description(classRoom.getDescription())
                    .isOnline(classRoom.isOnline())
                    .status(classRoom.getStatus())
                    .isActive(classRoom.isActive())
                    .startedAt(classRoom.getStartedAt())
                    .endedAt(classRoom.getEndedAt())
                    .instructorName(instructorName)
                    .studentCount(studentCount)
                    .build();
        }
    }

    /**
     * 페이지네이션 응답
     */
    @Data
    @Builder
    public static class ClassListPage {
        private List<ClassItem> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean last;
    }
}