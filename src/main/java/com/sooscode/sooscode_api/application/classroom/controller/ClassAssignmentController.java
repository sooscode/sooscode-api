package com.sooscode.sooscode_api.application.classroom.controller;

import com.sooscode.sooscode_api.application.classroom.dto.ClassAssignmentRequest;
import com.sooscode.sooscode_api.application.classroom.dto.ClassAssignmentResponse;
import com.sooscode.sooscode_api.application.classroom.service.ClassAssignmentService;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassAssignment;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classroom")
@RequiredArgsConstructor
@Slf4j
public class ClassAssignmentController {
    private final ClassAssignmentService classAssignmentService;

    // 클래스 담당 강사 추가

    @PostMapping("/assignment")
    public ResponseEntity<?> addClassAssignment(@RequestBody ClassAssignmentRequest rq) {

        log.info("addClassAssignment Controller 실행");

        classAssignmentService.addClassAssignment(rq);

        return ResponseEntity.ok("add class assignment successfully");
    }

    // 클래스 담당 강사 삭제
    @DeleteMapping("/assignment/{classId}")
    public ResponseEntity<?> deleteClassAssignment(@PathVariable Long classId) {
        log.info("deleteClassAssignment classId={}", classId);

        classAssignmentService.deleteClassAssignment(classId);

        return ResponseEntity.ok("delete class assignment successfully");
    }

    // 클래스 담당 강사 조회
    @GetMapping("/assignment")
    public ResponseEntity<?> getClassAssignments(@RequestParam Long classId) {

        log.info("getClassAssignments classId={}", classId);

        ClassAssignmentResponse response = classAssignmentService.getClassAssignment(classId);

        return ResponseEntity.ok(response);
    }

}
