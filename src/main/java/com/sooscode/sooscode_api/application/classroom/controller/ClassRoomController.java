package com.sooscode.sooscode_api.application.classroom.controller;

import com.sooscode.sooscode_api.application.classroom.service.ClassRoomService;
import com.sooscode.sooscode_api.application.classroom.dto.ClassRoomDetailResponse;
import com.sooscode.sooscode_api.global.response.ApiResponse;
import com.sooscode.sooscode_api.infra.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 클래스룸 관리 REST API
 * - 수업 종료
 * - 사용자 강제 퇴장
 */
@Slf4j
@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
public class ClassRoomController {

    private final ClassRoomService classRoomService;

    /**
     * 클래스 접근 권한 확인
     * @param classId
     * @param userDetails
     * @return classRoomDetail
     */
    @GetMapping("/{classId}")
    public ResponseEntity<ApiResponse<ClassRoomDetailResponse>> getClassRoom(
            @PathVariable Long classId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ClassRoomDetailResponse classRoomDetail = classRoomService
                .getClassRoomDetail(classId, userDetails.getUser().getUserId());

        return ApiResponse.ok(classRoomDetail);
    }

    /**
     * 수업 종료
     * POST /api/class/{classId}/end
     */
    @PostMapping("/{classId}/end")
    public ResponseEntity<Void> endClass(@PathVariable Long classId) {
        classRoomService.endClass(classId);
        return ResponseEntity.ok().build();
    }

    /**
     * 사용자 강제 퇴장
     * POST /api/class/{classId}/kick/{userId}
     */
    @PostMapping("/{classId}/kick/{userId}")
    public ResponseEntity<Void> kickUser(
            @PathVariable Long classId,
            @PathVariable Long userId,
            @RequestParam(required = false) String reason
    ) {
        classRoomService.kickUser(classId, userId, reason);
        return ResponseEntity.ok().build();
    }

    /**
     * 접속자 수 조회
     * GET /api/class/{classId}/count
     */
    @GetMapping("/{classId}/count")
    public ResponseEntity<CountResponse> getMemberCount(@PathVariable Long classId) {
        int count = classRoomService.getMemberCount(classId);
        return ResponseEntity.ok(new CountResponse(classId, count));
    }

    public record CountResponse(Long classId, int count) {}
}