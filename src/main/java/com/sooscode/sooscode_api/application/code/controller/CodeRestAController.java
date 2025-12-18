package com.sooscode.sooscode_api.application.code.controller;


import com.sooscode.sooscode_api.application.classroom.dto.ParticipantInfo;
import com.sooscode.sooscode_api.application.classroom.service.ParticipantService;
import com.sooscode.sooscode_api.application.classroom.store.ParticipantsResponse;
import com.sooscode.sooscode_api.application.code.dto.CodeData;
import com.sooscode.sooscode_api.application.code.dto.CodeResponse;
import com.sooscode.sooscode_api.application.code.service.CodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 코드/참여자 REST API
 * - 구독 전 초기 데이터 조회용
 */
@Slf4j
@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
public class CodeRestAController {

    private final CodeService codeService;
    private final ParticipantService participantService;

    // ==================== 코드 조회 ====================

    /**
     * 강사 코드 조회 (학생용 - CodeSharePanel에서 사용)
     * 또는 강사 본인 코드 조회 (새로고침 시)
     * GET /api/class/{classId}/code/instructor
     */
    @GetMapping("/{classId}/code/instructor")
    public ResponseEntity<CodeResponse> getInstructorCode(@PathVariable Long classId) {
        log.info("[CodeRestController] 강사 코드 조회 요청 - classId={}", classId);

        CodeData data = codeService.getInstructorCode(classId);

        if (data == null) {
            log.info("[CodeRestController] 강사 코드 없음 - classId={}, 빈 응답 반환", classId);
            return ResponseEntity.ok(CodeResponse.empty());
        }

        log.info("[CodeRestController] 강사 코드 조회 성공 - classId={}, userId={}, code.length={}, language={}",
                classId,
                data.getUserId(),
                data.getCode() != null ? data.getCode().length() : 0,
                data.getLanguage());

        return ResponseEntity.ok(CodeResponse.of(data));
    }

    /**
     * 학생 코드 조회 (강사가 학생 선택 시)
     * 또는 학생 본인 코드 조회 (새로고침 시)
     * GET /api/class/{classId}/code/student/{studentId}
     */
    @GetMapping("/{classId}/code/student/{studentId}")
    public ResponseEntity<CodeResponse> getStudentCode(
            @PathVariable Long classId,
            @PathVariable Long studentId
    ) {
        log.info("[CodeRestController] 학생 코드 조회 요청 - classId={}, studentId={}", classId, studentId);

        CodeData data = codeService.getStudentCode(classId, studentId);

        if (data == null) {
            log.info("[CodeRestController] 학생 코드 없음 - classId={}, studentId={}, 빈 응답 반환", classId, studentId);
            return ResponseEntity.ok(CodeResponse.empty());
        }

        log.info("[CodeRestController] 학생 코드 조회 성공 - classId={}, studentId={}, userId={}, code.length={}, language={}",
                classId,
                studentId,
                data.getUserId(),
                data.getCode() != null ? data.getCode().length() : 0,
                data.getLanguage());

        return ResponseEntity.ok(CodeResponse.of(data));
    }

    // ==================== 참여자 조회 ====================

    /**
     * 전체 참여자 목록
     * GET /api/class/{classId}/participants
     */
    @GetMapping("/{classId}/participants")
    public ResponseEntity<ParticipantsResponse> getParticipants(@PathVariable Long classId) {
        log.info("[CodeRestController] 참여자 목록 조회 요청 - classId={}", classId);

        String classIdStr = String.valueOf(classId);
        List<ParticipantInfo> participants = participantService.getParticipants(classIdStr);

        log.info("[CodeRestController] 참여자 목록 조회 성공 - classId={}, count={}", classId, participants.size());

        return ResponseEntity.ok(ParticipantsResponse.of(classIdStr, participants));
    }

    /**
     * 학생 목록만 조회 (강사용)
     * GET /api/class/{classId}/students
     */
    @GetMapping("/{classId}/students")
    public ResponseEntity<List<ParticipantInfo>> getStudents(@PathVariable Long classId) {
        log.info("[CodeRestController] 학생 목록 조회 요청 - classId={}", classId);

        List<ParticipantInfo> students = participantService.getStudents(String.valueOf(classId));

        log.info("[CodeRestController] 학생 목록 조회 성공 - classId={}, count={}", classId, students.size());

        return ResponseEntity.ok(students);
    }
}