package com.sooscode.sooscode_api.application.admin.controller;

import com.sooscode.sooscode_api.application.admin.dto.AdminClassRequest;
import com.sooscode.sooscode_api.application.admin.dto.AdminClassResponse;
import com.sooscode.sooscode_api.application.admin.dto.AdminPageResponse;
import com.sooscode.sooscode_api.application.admin.service.AdminClassService;
import com.sooscode.sooscode_api.global.response.ApiResponse;
import com.sooscode.sooscode_api.global.status.AdminStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        validateUpdate(
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
     * POST /api/admin/classes/{classId}/students/assign
     */
    @PostMapping("/{classId}/students/assign")
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
    public ResponseEntity<ApiResponse<AdminPageResponse<AdminClassResponse.ClassItem>>> getClassList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) LocalTime startTime,
            @RequestParam(required = false) LocalTime endTime,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        log.info("관리자 클래스 목록 조회: page={}, size={}, keyword={}", page, size, keyword);

        // 필터 객체 생성
        AdminClassRequest.SearchFilter filter = new AdminClassRequest.SearchFilter();
        filter.setKeyword(keyword);
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDirection);

        AdminPageResponse<AdminClassResponse.ClassItem> response = adminClassService.getClassList(filter, page, size);
        return ApiResponse.ok(AdminStatus.OK, response);
    }

    /**
     * 클래스 학생 목록 조회 (페이지네이션)
     * GET /api/admin/classes/{classId}/students?keyword={이름, 이메일}
     */
    @GetMapping("/{classId}/students")
    public ResponseEntity<ApiResponse<AdminPageResponse<AdminClassResponse.ClassStudentsResponse>>> getClassroomStudentsList(
            @PathVariable Long classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {

        // 필터 객체 생성
        AdminClassRequest.SearchFilter filter = new AdminClassRequest.SearchFilter();
        filter.setKeyword(keyword);
        filter.setStartDate(null);
        filter.setEndDate(null);
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDirection);

        AdminPageResponse<AdminClassResponse.ClassStudentsResponse> response =
                adminClassService.getClassStudentsList(classId, filter, page, size);
        return ApiResponse.ok(AdminStatus.OK, response);
    }

    /**
     * 강사 검색
     * GET /api/admin/classes/instructors?keyword={name, email}
     */
    @GetMapping("/instructors")
    public ResponseEntity<ApiResponse<List<AdminClassResponse.UserSearchItem>>> searchInstructors(
            @RequestParam(required = false) String keyword
    ) {
        log.info("강사 검색 요청: keyword={}", keyword);
        List<AdminClassResponse.UserSearchItem> instructors = adminClassService.searchInstructors(keyword);
        return ApiResponse.ok(AdminStatus.OK, instructors);
    }

    /**
     * 클래스에 속하지 않은 학생 검색
     * GET /api/admin/classes/{classId}/students/available?keyword={name, email}
     */
    @GetMapping("/{classId}/students/available")
    public ResponseEntity<ApiResponse<List<AdminClassResponse.UserSearchItem>>> searchAvailableStudents(
            @PathVariable Long classId,
            @RequestParam(required = false) String keyword
    ) {
        log.info("클래스 미참여 학생 검색 요청: classId={}, keyword={}", classId, keyword);
        List<AdminClassResponse.UserSearchItem> students = adminClassService.searchAvailableStudents(classId, keyword);
        return ApiResponse.ok(AdminStatus.OK, students);
    }

    /**
     * 클래스 정보 및 수강생 목록 엑셀 다운로드
     */
    @GetMapping("/{classId}/export")
    public ResponseEntity<byte[]> exportClassToExcel(@PathVariable Long classId) {
        log.info("클래스 엑셀 다운로드 요청: classId={}", classId);

        byte[] excelData = adminClassService.exportClassToExcel(classId);

        String filename = "class_" + classId + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);  // ← 이것만 남기기

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    /**
     * 전체 클래스 목록 엑셀 다운로드
     */
    @GetMapping("/list/export")
    public ResponseEntity<byte[]> exportClassListToExcel(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        log.info("클래스 목록 엑셀 다운로드 요청");

        AdminClassRequest.SearchFilter filter = new AdminClassRequest.SearchFilter();
        filter.setKeyword(keyword);
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDirection);

        byte[] excelData = adminClassService.exportClassListToExcel(filter);

        String filename = "classes_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);  // ← 이것만 남기기

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

}