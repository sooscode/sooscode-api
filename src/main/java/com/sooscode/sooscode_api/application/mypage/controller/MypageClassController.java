package com.sooscode.sooscode_api.application.mypage.controller;

import com.sooscode.sooscode_api.application.mypage.dto.MypageClassDetailResponse;
import com.sooscode.sooscode_api.application.mypage.dto.MypageMyclassesResponse;
import com.sooscode.sooscode_api.application.mypage.service.MypageClassService;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.response.ApiResponse;
import com.sooscode.sooscode_api.global.status.GlobalStatus;
import com.sooscode.sooscode_api.global.status.UserStatus;
import com.sooscode.sooscode_api.infra.security.CustomUserDetails;
import com.sooscode.sooscode_api.global.utils.FileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Slf4j
public class MypageClassController {

    private final MypageClassService mypageClassService;

    // 강의실 입장시 class의 data를 반환
    @GetMapping("/detail/{classId}")
    public ResponseEntity<ApiResponse<MypageClassDetailResponse>> getClassDetail(
            @PathVariable Long classId) {

        log.info("[Mypage] getClassDetail 요청 - classId={}", classId);

        FileValidator.validateClassId(classId);

        MypageClassDetailResponse response =
                mypageClassService.getClassDetail(classId);

        return ApiResponse.ok(GlobalStatus.OK, response);
    }

    // Instructor 및 student에 따라 가지고있는 class의 List를 반환
    @GetMapping("/classes")
    public ResponseEntity<ApiResponse<Page<MypageMyclassesResponse>>> getClasses(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("[Mypage] getClasses 요청 - page={}, size={}", page, size);

        User user = userDetails.getUser();
        UserRole userRole = user.getRole();
        Long userId = user.getUserId();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<MypageMyclassesResponse> response;

        if (userRole.equals(UserRole.STUDENT)) {
            response = mypageClassService.getStudentClasses(userId, pageable);
        } else if (userRole.equals(UserRole.INSTRUCTOR)) {
            response = mypageClassService.getTeacherClasses(userId, pageable);
        } else {
            throw new CustomException(UserStatus.SUSPENDED);
        }

        return ApiResponse.ok(GlobalStatus.OK, response);
    }
}
