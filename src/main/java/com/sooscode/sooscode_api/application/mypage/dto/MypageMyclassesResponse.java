package com.sooscode.sooscode_api.application.mypage.dto;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor

public class MypageMyclassesResponse {
        private Long classId;
        private String title;
        private String teacherName;
        private String thumbnailUrl;

    public static MypageMyclassesResponse from(ClassRoom classRoom) {
        return MypageMyclassesResponse.builder()
                .classId(classRoom.getClassId())
                .title(classRoom.getTitle())
                .teacherName(classRoom.getUser().getName()) // 구조에 따라 수정
                .thumbnailUrl(
                        classRoom.getFile() != null
                                ? classRoom.getFile().getUrl()
                                : "/images/default-thumbnail.png"
                )
                .build();
    }
}
