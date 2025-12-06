package com.sooscode.sooscode_api.application.classroom.dto.classroom;

import com.sooscode.sooscode_api.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
/**
 *  TeacherList 반환 DTO
 */
public class TeacherListItemResponse {
    private Long userId;
    private String userName;

    public static TeacherListItemResponse from(User user) {
        return TeacherListItemResponse.builder()
                .userId(user.getUserId())
                .userName(user.getName())
                .build();
    }
}
