package com.sooscode.sooscode_api.application.classroom.controller;

import com.sooscode.sooscode_api.application.classroom.service.TestServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 *  추후 userId 받아오는 방식 결정 되면 userId 받는부분 수정 예정
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final TestServiceImpl testService;

//    @PutMapping(value = "/profile", consumes = "multipart/form-data")
//    public ResponseEntity<?> updateProfileImage(
//            @RequestParam("userId") Long userId,
//            @RequestPart(required = false) MultipartFile photo
//    ) throws IOException {
//
//        testService.updateProfileImage(userId, photo);
//
//        return ResponseEntity.ok("프로필 이미지 수정 완료");
//    }


}

