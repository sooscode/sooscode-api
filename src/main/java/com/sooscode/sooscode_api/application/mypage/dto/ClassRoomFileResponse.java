package com.sooscode.sooscode_api.application.mypage.dto;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoomFile;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassRoomFileResponse {

    private Long classroomFileId;
    private Long fileId;
    private String fileName;
    private String fileUrl;
    private Long uploadedBy;
    private LocalDateTime createdAt;

    public static ClassRoomFileResponse from(ClassRoomFile entity) {
        return ClassRoomFileResponse.builder()
                .classroomFileId(entity.getClassroomFileId())
                .fileId(entity.getFile().getFileId())
                .fileName(entity.getFile().getOriginalName())   // SooFile 필드명에 따라 수정
                .fileUrl(entity.getFile().getUrl())
                .uploadedBy(entity.getUploadedBy() != null
                        ? entity.getUploadedBy().getUserId()
                        : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
