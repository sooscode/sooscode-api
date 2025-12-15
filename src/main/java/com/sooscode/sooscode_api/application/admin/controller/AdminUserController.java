package com.sooscode.sooscode_api.application.admin.controller;

import com.sooscode.sooscode_api.application.admin.dto.AdminPageResponse;
import com.sooscode.sooscode_api.application.admin.dto.AdminUserRequest;
import com.sooscode.sooscode_api.application.admin.dto.AdminUserResponse;
import com.sooscode.sooscode_api.application.admin.service.AdminUserService;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.global.api.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<AdminUserResponse.UserCreated>> createInstructor(
            @RequestBody AdminUserRequest.Create request
    ) {
        validateUsername(request.getName());
        validateEmail(request.getEmail());
        UserRole role = validateRole(request.getRole());
        log.info("관리자 강사 계정 생성 요청: email={}", request.getEmail());
        AdminUserResponse.UserCreated response = adminUserService.createUser(request, role);
        return ApiResponse.ok(response);
    }


    /**
     * 일괄 계정 생성 (CSV)
     * POST /api/admin/users/bulk
     */
    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<AdminUserResponse.CreateResult>> bulkCreateUsers() {
        return ApiResponse.ok(null, null);
    }

    /**
     * 사용자 상세 조회
     * GET /api/admin/users/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AdminUserResponse.Detail>> getUserDetail(@PathVariable Long userId) {
        return ApiResponse.ok(null, null);
    }

    /**
     * 사용자 로그인 히스토리 조회
     * GET /api/admin/users/{userId}/login-history?limit=5
     */
    @GetMapping("/{userId}/login-history")
    public ResponseEntity<ApiResponse<AdminPageResponse<AdminUserResponse.UserHistory>>> getLoginHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ApiResponse.ok(null, null);
    }

    /**
     * 사용자 목록 조회 (페이지네이션 + 필터링)
     * GET /api/admin/users?page=0&size=10&keyword=test&role=INSTRUCTOR
     */
    @GetMapping
    public ResponseEntity<ApiResponse<AdminPageResponse<Void>>> getUserList(
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

        return ApiResponse.ok(null, null);
    }
}