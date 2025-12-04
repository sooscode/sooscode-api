package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.ClassAssignmentRequest;
import com.sooscode.sooscode_api.application.classroom.dto.ClassAssignmentResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClassAssignmentService {
    void addClassAssignment(ClassAssignmentRequest rq);
    ClassAssignmentResponse getClassAssignment(Long classId);
    void deleteClassAssignment(Long classId);
}
