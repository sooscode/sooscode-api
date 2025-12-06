package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.participant.ClassParticipantResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClassParticipantService {

    /** 해당 클래스에 참여 중인 학생 목록 조회 */
    List<ClassParticipantResponse> getClassStudents(Long classId);
}
