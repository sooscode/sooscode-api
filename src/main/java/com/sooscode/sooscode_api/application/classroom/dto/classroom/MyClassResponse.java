package com.sooscode.sooscode_api.application.classroom.dto.classroom;

import lombok.Getter;

@Getter
/**
 *  My class Info DTO
 */
public class MyClassResponse {

    private Long classId;
    private String title;
    private String thumbnailUrl;
    private String teacherName;

    public MyClassResponse(Long classId, String title, String thumbnailUrl, String teacherName) {
        this.classId = classId;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.teacherName = teacherName;
    }
}

