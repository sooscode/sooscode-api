package com.sooscode.sooscode_api.application.classroom.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ParticipantMessage {
    private Long classId;
    private List<ParticipantInfo> participant;
}