package com.sooscode.sooscode_api.application.classroom.dto.participant;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 *  Default ClassParticipant response DTO
 */
public class ClassParticipantResponse {

    private Long participantId;
    private Long userId;
    private String userName;
    private Long classId;
    private String userEmail;

    public static ClassParticipantResponse from(ClassParticipant entity) {
        return ClassParticipantResponse.builder()
                .participantId(entity.getClassParticipantId())
                .userId(entity.getUser().getUserId())
                .userName(entity.getUser().getName())
                .classId(entity.getClassRoom().getClassId())
                .userEmail(entity.getUser().getEmail())
                .build();
    }
}
