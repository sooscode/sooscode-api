package com.sooscode.sooscode_api.application.classroom.store;

import com.sooscode.sooscode_api.application.classroom.dto.ParticipantInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 참여자 목록 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantsResponse {

    private String classId;
    private List<ParticipantInfo> participants;
    private int totalCount;
    private int studentCount;

    public static ParticipantsResponse of(String classId, List<ParticipantInfo> participants) {
        int studentCount = (int) participants.stream()
                .filter(p -> !p.isInstructor())
                .count();

        return ParticipantsResponse.builder()
                .classId(classId)
                .participants(participants)
                .totalCount(participants.size())
                .studentCount(studentCount)
                .build();
    }
}