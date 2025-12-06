package com.sooscode.sooscode_api.application.classroom.dto.classroom;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
/**
 *  Default ClassRoom response DTO
 */
public class ClassRoomResponse {

    private Long classId;
    private boolean online;
    private String title;
    private String description;
    private String status;
    private String mode;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public static ClassRoomResponse from(ClassRoom classRoom) {
        return ClassRoomResponse.builder()
                .classId(classRoom.getClassId())
                .online(classRoom.isOnline())
                .title(classRoom.getTitle())
                .description(classRoom.getDescription())
                .status(classRoom.getStatus() != null ? classRoom.getStatus().name() : null)
                .mode(classRoom.getMode().name())
                .startedAt(classRoom.getStartedAt())
                .endedAt(classRoom.getEndedAt())
                .build();
    }

    // Test용 내부클래스
    @Getter
    @Builder
    public static class Detail {

        private Long classId;
        private String title;
        private String description;
        private String mode;
        private String status;
        private int maxStudents;
        private LocalDateTime startedAt;
        private LocalDateTime endedAt;

        public static Detail from(ClassRoom classRoom) {
            return Detail.builder()
                    .classId(classRoom.getClassId())
                    .title(classRoom.getTitle())
                    .description(classRoom.getDescription())
                    .mode(classRoom.getMode().name())
                    .status(classRoom.getStatus().name())
                    .startedAt(classRoom.getStartedAt())
                    .endedAt(classRoom.getEndedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Summary {
        private Long classId;
        private String title;
        private boolean online;
        private String status;    // UPCOMING / ONGOING / FINISHED
        private LocalDateTime startedAt;
        private LocalDateTime endedAt;
    }

    @Getter
    @Builder
    public static class Stats {
        private Long classId;
        private int participantCount;
        private int snapshotCount;
        private LocalDateTime lastSnapshotAt;
    }

}