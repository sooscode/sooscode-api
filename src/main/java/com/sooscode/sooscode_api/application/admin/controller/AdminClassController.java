package com.sooscode.sooscode_api.application.admin.controller;

import com.sooscode.sooscode_api.application.admin.dto.AdminClassRequest;
import com.sooscode.sooscode_api.application.admin.dto.AdminClassResponse;
import com.sooscode.sooscode_api.application.admin.service.AdminClassService;
import com.sooscode.sooscode_api.domain.classroom.enums.ClassStatus;
import com.sooscode.sooscode_api.global.api.response.ApiResponse;
import com.sooscode.sooscode_api.global.api.status.AdminStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.sooscode.sooscode_api.global.utils.ClassValidator.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/classes")
@RequiredArgsConstructor
public class AdminClassController {

    private final AdminClassService adminClassService;

    /**
     * 클래스 생성
     * POST /api/admin/classes/create
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AdminClassResponse.ClassItem>> createClass(
            @RequestBody AdminClassRequest.Create request
    ) {
        log.info("관리자 클래스 생성 요청: title={}", request.getTitle());
        validateClass(
                request.getTitle(),
                request.getDescription(),
                request.getInstructorId(),
                request.getIsOnline(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStartTime(),
                request.getEndTime()
                );
        AdminClassResponse.ClassItem response = adminClassService.createClass(request);
        return ApiResponse.ok(AdminStatus.CLASS_CREATE_SUCCESS, response);
    }

    /**
     * 클래스 상세 조회
     * GET /api/admin/classes/{classId}
     */
    @GetMapping("/{classId}")
    public ResponseEntity<ApiResponse<AdminClassResponse.ClassItem>> getClassDetail(@PathVariable Long classId) {
        log.info("관리자 클래스 상세 조회: classId={}", classId);
        AdminClassResponse.ClassItem response = adminClassService.getClassDetail(classId);
        return ApiResponse.ok(AdminStatus.OK, response);
    }

    /**
     * 클래스 수정
     * POST /api/admin/classes/{classId}/edit
     */
    @PostMapping("/{classId}/edit")
    public ResponseEntity<ApiResponse<AdminClassResponse.ClassItem>> updateClass(
            @PathVariable Long classId,
            @RequestBody AdminClassRequest.Update request
    ) {
        validateClass(
                request.getTitle(),
                request.getDescription(),
                request.getInstructorId(),
                request.getIsOnline(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStartTime(),
                request.getEndTime()
        );
        validateInstructorId(request.getInstructorId());
        log.info("관리자 클래스 수정 요청: classId={}", classId);
        AdminClassResponse.ClassItem response = adminClassService.updateClass(classId, request);
        return ApiResponse.ok(AdminStatus.OK, response);
    }

    /**
     * 클래스 삭제 (비활성화)
     * POST /api/admin/classes/{classId}/delete
     */
    @PostMapping("/{classId}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable Long classId) {
        log.info("관리자 클래스 삭제 요청: classId={}", classId);
        adminClassService.deleteClass(classId);
        return ApiResponse.ok(AdminStatus.OK);
    }

    /**
     * 학생 배정
     * POST /api/admin/classes/{classId}/students
     */
    @PostMapping("/{classId}/students")
    public ResponseEntity<ApiResponse<AdminClassResponse.StudentOperationResponse>> assignStudents(
            @PathVariable Long classId,
            @RequestBody AdminClassRequest.Students request
    ) {
        log.info("학생 일괄 배정 요청: classId={}, 학생 수={}", classId, request.getStudentIds().size());
        validateStudentIds(request.getStudentIds());
        AdminClassResponse.StudentOperationResponse response =
                adminClassService.assignStudents(classId, request);
        return ApiResponse.ok(AdminStatus.OK, response);
    }

    /**
     * 학생 배정 취소
     *  POST /api/admin/classes/{classId}/student/{studentId}/delete
     */
    @PostMapping("/{classId}/students/delete")
    public ResponseEntity<ApiResponse<AdminClassResponse.StudentOperationResponse>> deleteStudents(
            @PathVariable Long classId,
            @RequestBody AdminClassRequest.Students request
    ) {
        log.info("학생 일괄 삭제 요청: classId={}, 학생 수={}", classId, request.getStudentIds().size());
        validateStudentIds(request.getStudentIds());
        AdminClassResponse.StudentOperationResponse response =
                adminClassService.deleteStudents(classId, request);
        return ApiResponse.ok(AdminStatus.OK, response);
    }

    /**
     * 클래스 목록 조회 (페이지네이션 + 필터링)
     * GET /api/admin/classes?page=0&size=10&keyword=java&status=ONGOING
     */
    @GetMapping
    public ResponseEntity<ApiResponse<AdminClassResponse.PageResponse>> getClassList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ClassStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        log.info("관리자 클래스 목록 조회: page={}, size={}, keyword={}", page, size, keyword);

        // 필터 객체 생성
        AdminClassRequest.SearchFilter filter = new AdminClassRequest.SearchFilter();
        filter.setKeyword(keyword);
        filter.setStatus(status);
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDirection);

        AdminClassResponse.PageResponse response = adminClassService.getClassList(filter, page, size);
        return ApiResponse.ok(AdminStatus.OK, response);
    }
}