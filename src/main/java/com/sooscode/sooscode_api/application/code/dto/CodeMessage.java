package com.sooscode.sooscode_api.application.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket 코드 공유 메시지 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeMessage {

    private Long classId;
    private Long userId;
    private String username;
    private String code;
    private String language;
    private boolean instructor;
    /**
     * 강사가 학생 코드를 수정한 경우 true
     * - true: 학생은 에디터에 반영, 강사는 무시
     * - false: 강사는 에디터에 반영, 학생은 무시 (본인이 타이핑했으므로)
     */
    @Builder.Default
    private boolean editedByInstructor = false;
}