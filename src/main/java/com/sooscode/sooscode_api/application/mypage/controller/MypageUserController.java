package com.sooscode.sooscode_api.application.mypage.controller;

import com.sooscode.sooscode_api.application.classroom.service.ClassRoomService;
import com.sooscode.sooscode_api.application.mypage.dto.MypageUserUpdatePasswordRequest;
import com.sooscode.sooscode_api.application.mypage.dto.MypageUserUpdateProfileRequest;
import com.sooscode.sooscode_api.application.mypage.dto.MypageUserUpdateResponse;
import com.sooscode.sooscode_api.application.mypage.service.MypageClassService;
import com.sooscode.sooscode_api.application.mypage.service.MypageUserService;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.response.ApiResponse;
import com.sooscode.sooscode_api.global.api.status.GlobalStatus;
import com.sooscode.sooscode_api.global.api.status.UserStatus;
import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Slf4j
public class MypageUserController {

    private final MypageUserService mypageService;
    private final ClassRoomService classRoomService;
    private final MypageClassService mypageClassService;

    /**
     * 비밀번호 변경
     */
    @PostMapping("/password/update")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MypageUserUpdatePasswordRequest request
    ) {
        mypageService.updatePassword(userDetails.getUser(), request);

        return ApiResponse.ok(GlobalStatus.OK);
    }

    /**
     * 프로필 수정
     */
    @PostMapping("/profile/update")
    public ResponseEntity<ApiResponse<MypageUserUpdateResponse>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MypageUserUpdateProfileRequest request
    ) {
        User user = userDetails.getUser();
        User updated = mypageService.updateProfile(user, request);

        MypageUserUpdateResponse response = new MypageUserUpdateResponse(
            updated.getName()
        );

        return ApiResponse.ok(GlobalStatus.OK, response);
    }

    /**
     * 회원 탈퇴
     */
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        mypageService.deleteUser(user);

        return ApiResponse.ok(GlobalStatus.OK);
    }

    /**
     * 프로필 이미지 변경
     */
    @PostMapping(value = "/profile/image/update", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Void>> uploadProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) throws IOException {

        Long userId = userDetails.getUser().getUserId();
        mypageService.updateProfileImage(userId, photo);

        return ApiResponse.ok(GlobalStatus.OK);
    }

    /**
     * 프로필 이미지 삭제
     */
    @PostMapping("/profile/image/delete")
    public  ResponseEntity<ApiResponse<Void>> deleteProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Long userId = userDetails.getUser().getUserId();
        mypageService.deleteProfileImage(userId);

        return ApiResponse.ok(GlobalStatus.OK);
    }

    // 썸네일 등록
    @PostMapping(
            value = "/classroom/{classId}/thumbnail",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<ApiResponse<Void>> uploadClassRoomThumbnail(
            @PathVariable Long classId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) throws IOException {

        log.info("thumbnailupload 시도");

        UserRole userRole = userDetails.getUser().getRole();


        classRoomService.updateThumbnail(
                classId,
                userDetails.getUser().getUserId(),
                thumbnail
        );

        return ApiResponse.ok(GlobalStatus.OK);
    }



}
