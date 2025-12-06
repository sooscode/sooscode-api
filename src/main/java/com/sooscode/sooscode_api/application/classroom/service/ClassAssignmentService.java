package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.assignment.ClassAssignmentRequest;
import com.sooscode.sooscode_api.application.classroom.dto.assignment.ClassAssignmentResponse;
import com.sooscode.sooscode_api.application.classroom.dto.classroom.TeacherListItemResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClassAssignmentService {
    /** 특정 클래스의 담당 강사 정보 조회 */
    ClassAssignmentResponse getClassAssignment(Long classId);

    /** 전체 클래스 기준 담당된 강사 목록 조회(중복 제거) */
    List<TeacherListItemResponse> getAssignmentTeachers();
}
