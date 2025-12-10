package com.sooscode.sooscode_api.application.admin.controller;

import com.sooscode.sooscode_api.application.admin.dto.AdminClassRequest;
import com.sooscode.sooscode_api.application.admin.dto.AdminClassResponse;
import com.sooscode.sooscode_api.application.admin.service.AdminClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/classes")
@RequiredArgsConstructor
public class AdminClassController {

    private final AdminClassService adminClassService;

    /**
     * 클래스 생성
     * POST /api/admin/class
     */
    @PostMapping
    public ResponseEntity<AdminClassResponse.Detail> createClass(
            @Valid @RequestBody AdminClassRequest.Create request
    ) {
        log.info("관리자 클래스 생성 요청: title={}", request.getTitle());
        AdminClassResponse.Detail response = adminClassService.createClass(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 클래스 수정
     * PUT /api/admin/class/{classId}
     */
    @PutMapping("/{classId}")
    public ResponseEntity<AdminClassResponse.Detail> updateClass(
            @PathVariable Long classId,
            @Valid @RequestBody AdminClassRequest.Update request
    ) {
        log.info("관리자 클래스 수정 요청: classId={}", classId);
        AdminClassResponse.Detail response = adminClassService.updateClass(classId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 클래스 삭제 (비활성화)
     * DELETE /api/admin/classes/{classId}
     */
    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long classId) {
        log.info("관리자 클래스 삭제 요청: classId={}", classId);
        adminClassService.deleteClass(classId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 클래스 목록 조회 (페이지네이션 + 필터링)
     * GET /api/admin/classes?page=0&size=10&keyword=java&status=ONGOING
     */
//    @GetMapping
//    public ResponseEntity<AdminClassResponse.PageResponse> getClassList(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(required = false) String keyword,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) String startDate,
//            @RequestParam(required = false) String endDate,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "DESC") String sortDirection
//    ) {
//        log.info("관리자 클래스 목록 조회: page={}, size={}, keyword={}", page, size, keyword);
//
//        // 필터 객체 생성
//        AdminClassRequest.SearchFilter filter = new AdminClassRequest.SearchFilter();
//        filter.setKeyword(keyword);
//        // status, startDate, endDate 파싱 로직 추가 필요
//        filter.setSortBy(sortBy);
//        filter.setSortDirection(sortDirection);
//
//        AdminClassResponse.PageResponse response = adminClassService.getClassList(filter, page, size);
//        return ResponseEntity.ok(response);
//    }

    /**
     * 클래스 상세 조회
     * GET /api/admin/classes/{classId}
     */
    @GetMapping("/{classId}")
    public ResponseEntity<AdminClassResponse.Detail> getClassDetail(@PathVariable Long classId) {
        log.info("관리자 클래스 상세 조회: classId={}", classId);
        AdminClassResponse.Detail response = adminClassService.getClassDetail(classId);
        return ResponseEntity.ok(response);
    }

    /**
     * 강사 배정
     * POST /api/admin/classes/{classId}/instructor
     */
    @PostMapping("/{classId}/instructor")
    public ResponseEntity<Void> assignInstructor(
            @PathVariable Long classId,
            @Valid @RequestBody AdminClassRequest.AssignInstructor request
    ) {
        log.info("강사 배정 요청: classId={}, instructorId={}", classId, request.getInstructorId());
        adminClassService.assignInstructor(classId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 학생 일괄 배정
     * POST /api/admin/classes/{classId}/students
     */
    @PostMapping("/{classId}/students")
    public ResponseEntity<Void> assignStudents(
            @PathVariable Long classId,
            @Valid @RequestBody AdminClassRequest.AssignStudents request
    ) {
        log.info("학생 일괄 배정 요청: classId={}, 학생 수={}", classId, request.getStudentIds().size());
        adminClassService.assignStudents(classId, request);
        return ResponseEntity.ok().build();
    }
}