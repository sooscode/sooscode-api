package com.sooscode.sooscode_api.application.classroom.controller;

import com.sooscode.sooscode_api.application.classroom.dto.participant.ClassParticipantResponse;
import com.sooscode.sooscode_api.application.classroom.service.ClassParticipantService;
import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long classId) {

        log.info("getClassParticipants classId={}", classId);

        List<ClassParticipantResponse> responses = classParticipantService.getClassStudents(classId);

        return ResponseEntity.ok(responses);
    }
}

