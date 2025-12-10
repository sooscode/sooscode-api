package com.sooscode.sooscode_api.application.admin.controller;

import com.sooscode.sooscode_api.application.admin.dto.AdminUserRequest;
import com.sooscode.sooscode_api.application.admin.dto.AdminUserResponse;
import com.sooscode.sooscode_api.application.admin.service.AdminUserService;
import com.sooscode.sooscode_api.global.api.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sooscode.sooscode_api.global.utils.UserValidator.validateEmail;
import static com.sooscode.sooscode_api.global.utils.UserValidator.validateUsername;


@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * 강사 계정 생성
     * POST /api/admin/users/instructor
     */
    @PostMapping("/instructor")
    public ResponseEntity<ApiResponse<AdminUserResponse.InstructorCreated>> createInstructor(
            @Valid @RequestBody AdminUserRequest.CreateInstructor request
    ) {
        validateUsername(request.getName());
        validateEmail(request.getEmail());
        log.info("관리자 강사 계정 생성 요청: email={}", request.getEmail());
        AdminUserResponse.InstructorCreated response = adminUserService.createInstructor(request);
        return ApiResponse.ok(response);
    }

    /**
     * 사용자 목록 조회 (페이지네이션 + 필터링)
     * GET /api/admin/users?page=0&size=10&keyword=test&role=INSTRUCTOR
     */
    /*
    @GetMapping
    public ResponseEntity<AdminUserResponse.PageResponse> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        log.info("관리자 사용자 목록 조회: page={}, size={}, keyword={}", page, size, keyword);

        // 필터 객체 생성
        AdminUserRequest.SearchFilter filter = new AdminUserRequest.SearchFilter();
        filter.setKeyword(keyword);
        filter.setIsActive(isActive);
        // role, startDate, endDate 파싱 로직 추가 필요
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDirection);

        AdminUserResponse.PageResponse response = adminUserService.getUserList(filter, page, size);
        return ResponseEntity.ok(response);
    }
     */

    /**
     * 사용자 상세 조회
     * GET /api/admin/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<AdminUserResponse.Detail> getUserDetail(@PathVariable Long userId) {
        log.info("관리자 사용자 상세 조회: userId={}", userId);
        AdminUserResponse.Detail response = adminUserService.getUserDetail(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 로그인 히스토리 조회
     * GET /api/admin/users/{userId}/login-history?limit=5
     */
//    @GetMapping("/{userId}/login-history")
//    public ResponseEntity<List<AdminUserResponse.LoginHistory>> getLoginHistory(
//            @PathVariable Long userId,
//            @RequestParam(defaultValue = "5") int limit
//    ) {
//        log.info("관리자 사용자 로그인 히스토리 조회: userId={}, limit={}", userId, limit);
//        List<AdminUserResponse.LoginHistory> response = adminUserService.getLoginHistory(userId, limit);
//        return ResponseEntity.ok(response);
//    }

    /**
     * 사용자 통계 조회
     * GET /api/admin/users/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<AdminUserResponse.Statistics> getUserStatistics() {
        log.info("관리자 사용자 통계 조회");
        AdminUserResponse.Statistics response = adminUserService.getUserStatistics();
        return ResponseEntity.ok(response);
    }

    /**
     * 일괄 계정 생성 (CSV)
     * POST /api/admin/users/bulk
     */
//    @PostMapping("/bulk")
//    public ResponseEntity<AdminUserResponse.BulkCreateResult> bulkCreateUsers(
//            @Valid @ModelAttribute AdminUserRequest.BulkCreate request
//    ) {
//        log.info("관리자 일괄 계정 생성 요청: role={}", request.getRole());
//        AdminUserResponse.BulkCreateResult response = adminUserService.bulkCreateUsers(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
}