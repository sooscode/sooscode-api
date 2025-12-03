package com.sooscode.sooscode_api.application.classroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassParticipantResponse {

    private Long participantId;       // class_participant PK
    private Long userId;              // user PK
    private String name;              // 유저 이름
    private String profileImageUrl;   // 프로필 이미지 URL
    private String role;              // STUDENT / TEACHER
    private Long classId;             // 소속 클래스 ID
}
