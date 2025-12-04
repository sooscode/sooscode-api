package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.ClassParticipantResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClassParticipantService {
    List<ClassParticipantResponse> getClassStudents(Long classId);
    void addParticipant(Long classId, Long userId);
    void deleteParticipant(Long classId, Long userId);
}
