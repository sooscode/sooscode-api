package com.sooscode.sooscode_api.application.classroom.controller;

import com.sooscode.sooscode_api.application.classroom.dto.ClassParticipantResponse;
import com.sooscode.sooscode_api.application.classroom.service.ClassParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classroom")
@RequiredArgsConstructor
@Slf4j
public class ClassParticipantController {

    private final ClassParticipantService classParticipantService;

    // class 참가자 조회
    @GetMapping("/participant/{classId}")
    public ResponseEntity<?> getClassParticipants(
            @PathVariable Long classId) {

        log.info("getClassParticipants classId={}", classId);

        List<ClassParticipantResponse> responses = classParticipantService.getClassStudents(classId);

        return ResponseEntity.ok(responses);
    }

    // class 참가자 생성
    @PostMapping("/participant/{classId}/{userId}")
    public ResponseEntity<?> addClassParticipant(
            @PathVariable Long classId,
            @PathVariable Long userId) {

        classParticipantService.addParticipant(classId, userId);

        return ResponseEntity.ok("add participant successfully");
    }

    // class 참가자 삭제
    @DeleteMapping("/participant/{classId}/{userId}")
    public ResponseEntity<?> deleteClassParticipant(
            @PathVariable Long classId,
            @PathVariable Long userId){
        classParticipantService.deleteParticipant(classId, userId);

        return ResponseEntity.ok("delete participant successfully");
    }
}

