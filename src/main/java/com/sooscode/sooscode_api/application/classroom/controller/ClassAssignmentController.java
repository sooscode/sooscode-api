package com.sooscode.sooscode_api.application.classroom.controller;

import com.sooscode.sooscode_api.application.classroom.dto.assignment.ClassAssignmentRequest;
import com.sooscode.sooscode_api.application.classroom.dto.assignment.ClassAssignmentResponse;
import com.sooscode.sooscode_api.application.classroom.service.ClassAssignmentService;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.errorcode.ClassErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classroom")
@RequiredArgsConstructor
@Slf4j
public class ClassAssignmentController {
    private final ClassAssignmentService classAssignmentService;

    // 클래스 담당 강사 조회
    @GetMapping("/assignment")
    public ResponseEntity<?> getClassAssignments(@RequestParam Long classId) {

        log.info("getClassAssignments classId={}", classId);

        validateClassId(classId);

        ClassAssignmentResponse response = classAssignmentService.getClassAssignment(classId);

        return ResponseEntity.ok(response);
    }

    // 수업에 담당된 모든 강사 조회 ( 중복제거 )
    @GetMapping("/assignment/teachers")
    public ResponseEntity<?> getClassAssignmentTeachers() {

        log.info("getClassAssignmentTeachers");

        var response = classAssignmentService.getAssignmentTeachers();

        return ResponseEntity.ok(response);
    }

    private void validateClassId(Long classId) {
        if (classId == null || classId <= 0) {
            throw new CustomException(ClassErrorCode.INVALID_CLASS_ID);
        }
    }

    private void validateRequest(ClassAssignmentRequest rq) {
        if (rq == null) {
            throw new CustomException(ClassErrorCode.INVALID_REQUEST);
        }
        if (rq.getClassId() == null || rq.getClassId() <= 0) {
            throw new CustomException(ClassErrorCode.INVALID_CLASS_ID);
        }
        if (rq.getUserId() == null || rq.getUserId() <= 0) {
            throw new CustomException(ClassErrorCode.INVALID_USER_ID);
        }
    }
}
