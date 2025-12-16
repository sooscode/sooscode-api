package com.sooscode.sooscode_api.application.admin.controller;

import com.sooscode.sooscode_api.application.admin.dto.AdminPageResponse;
import com.sooscode.sooscode_api.application.admin.dto.AdminUserRequest;
import com.sooscode.sooscode_api.application.admin.dto.AdminUserResponse;
import com.sooscode.sooscode_api.application.admin.service.AdminUserService;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import com.sooscode.sooscode_api.global.api.response.ApiResponse;
import com.sooscode.sooscode_api.global.api.status.AdminStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.sooscode.sooscode_api.global.utils.ExcelFileValidator.validateExcelFile;
import static com.sooscode.sooscode_api.global.utils.UserValidator.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * 계정 생성
     * POST /api/admin/users/create
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AdminUserResponse.UserCreated>> createUser(
            @RequestBody AdminUserRequest.Create request
    ) {
        validateUsername(request.getName());
        validateEmail(request.getEmail());
        UserRole role = validateRole(request.getRole());

        log.info("관리자 계정 생성 요청: email={}, role={}", request.getEmail(), role);

        AdminUserResponse.UserCreated response = adminUserService.createUser(request, role);
        return ApiResponse.ok(AdminStatus.OK, response);
    }

    /**
     * 일괄 계정 생성 (Excel) - 결과를 Excel로 반환
     * POST /api/admin/users/bulk
     */
    @PostMapping("/bulk")
    public ResponseEntity<byte[]> bulkCreateUsers(
            @RequestParam("file") MultipartFile excelFile
    ) {
        // 파일 검증 (확장자, MIME타입, 시그니처, 구조 등)
        validateExcelFile(excelFile);

        log.info("일괄 계정 생성 요청: fileName={}", excelFile.getOriginalFilename());

        AdminUserRequest.BulkCreate request = new AdminUserRequest.BulkCreate(excelFile, null);
        byte[] resultExcel = adminUserService.bulkCreateUsers(request);

        String filename = "bulk_create_result_" +
                LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(resultExcel.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resultExcel);
    }

    /**
     * 사용자 목록 조회 (페이지네이션 + 필터링)
     * GET /api/admin/users?page=0&size=10&keyword=test&role=INSTRUCTOR
     */
    @GetMapping
    public ResponseEntity<ApiResponse<AdminPageResponse<AdminUserResponse.UserListItem>>> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        log.info("사용자 목록 조회: page={}, size={}, keyword={}, role={}", page, size, keyword, role);

        AdminUserRequest.SearchFilter filter = buildSearchFilter(
                keyword, role, status, startDate, endDate, sortBy, sortDirection
        );

        AdminPageResponse<AdminUserResponse.UserListItem> response =
                adminUserService.getUserList(filter, page, size);

        return ApiResponse.ok(AdminStatus.OK, response);
    }

    /**
     * 사용자 상세 조회
     * GET /api/admin/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AdminUserResponse.Detail>> getUserDetail(
            @PathVariable Long userId
    ) {
        log.info("사용자 상세 조회: userId={}", userId);

        AdminUserResponse.Detail response = adminUserService.getUserDetail(userId);
        return ApiResponse.ok(AdminStatus.OK, response);
    }

    /**
     * 사용자 히스토리 조회
     * GET /api/admin/users/{userId}/history?limit=5
     */
    @GetMapping("/{userId}/history")
    public ResponseEntity<ApiResponse<List<AdminUserResponse.UserHistory>>> getUserHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit
    ) {
        log.info("사용자 히스토리 조회: userId={}, limit={}", userId, limit);

        List<AdminUserResponse.UserHistory> response = adminUserService.getUserHistory(userId, limit);
        return ApiResponse.ok(AdminStatus.OK, response);
    }

    /**
     * 사용자 삭제 (비활성화)
     * POST /api/admin/users/{userId}/delete
     */
    @PostMapping("/{userId}/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long userId
    ) {
        log.info("사용자 삭제 요청: userId={}", userId);

        adminUserService.deleteUser(userId);
        return ApiResponse.ok(AdminStatus.OK, null);
    }

    /**
     * 사용자 활성화
     * POST /api/admin/users/{userId}/activate
     */
    @PostMapping("/{userId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateUser(
            @PathVariable Long userId
    ) {
        log.info("사용자 활성화 요청: userId={}", userId);

        adminUserService.activeUser(userId);
        return ApiResponse.ok(AdminStatus.OK, null);
    }

    /**
     * 사용자 역할 변경
     * POST /api/admin/users/{userId}/role
     */
    @PostMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<Void>> changeUserRole(
            @PathVariable Long userId,
            @RequestBody AdminUserRequest.ChangeRole request
    ) {
        log.info("사용자 역할 변경 요청: userId={}, newRole={}", userId, request.getRole());

        adminUserService.changeUserRole(userId, request);
        return ApiResponse.ok(AdminStatus.OK, null);
    }

    /**
     * 사용자 목록 엑셀 다운로드
     * GET /api/admin/users/export
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportUsersToExcel(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        log.info("사용자 엑셀 다운로드 요청");

        AdminUserRequest.SearchFilter filter = buildSearchFilter(
                keyword, role, status, startDate, endDate, sortBy, sortDirection
        );

        byte[] excelData = adminUserService.exportUsersToExcel(filter);

        String filename = "users_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(excelData.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }

    /**
     * 일괄 생성용 엑셀 템플릿 다운로드
     * GET /api/admin/users/template/download
     */
    @GetMapping("/template/download")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        log.info("엑셀 템플릿 다운로드 요청");

        byte[] templateData = adminUserService.generateExcelTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "user_upload_template.xlsx");
        headers.setContentLength(templateData.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(templateData);
    }

    // ==================== 헬퍼 메서드 ====================

    /**
     * 검색 필터 DTO 빌드
     */
    private AdminUserRequest.SearchFilter buildSearchFilter(
            String keyword,
            String roleStr,
            String statusStr,
            String startDateStr,
            String endDateStr,
            String sortBy,
            String sortDirection
    ) {
        UserRole role = null;
        if (roleStr != null && !roleStr.isBlank()) {
            try {
                role = UserRole.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 역할 값: {}", roleStr);
            }
        }

        UserStatus status = null;
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                status = UserStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 상태 값: {}", statusStr);
            }
        }

        LocalDateTime startDate = null;
        if (startDateStr != null && !startDateStr.isBlank()) {
            startDate = LocalDate.parse(startDateStr).atStartOfDay();
        }

        LocalDateTime endDate = null;
        if (endDateStr != null && !endDateStr.isBlank()) {
            endDate = LocalDate.parse(endDateStr).atTime(LocalTime.MAX);
        }

        return new AdminUserRequest.SearchFilter(
                keyword, role, status, startDate, endDate, sortBy, sortDirection
        );
    }

    /**
     * 특정 유저가 수강 중인 클래스 목록 조회
     * GET /api/admin/users/{userId}/classes
     */
    @GetMapping("/{userId}/classes")
    public ResponseEntity<ApiResponse<List<AdminUserResponse.EnrolledClass>>> getUserEnrolledClasses(
            @PathVariable Long userId
    ) {
        log.info("유저 수강 클래스 목록 조회: userId={}", userId);

        List<AdminUserResponse.EnrolledClass> response = adminUserService.getUserEnrolledClasses(userId);
        return ApiResponse.ok(AdminStatus.OK, response);
    }
}