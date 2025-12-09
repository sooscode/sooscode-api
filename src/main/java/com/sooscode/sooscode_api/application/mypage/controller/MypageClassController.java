package com.sooscode.sooscode_api.application.mypage.controller;

import com.sooscode.sooscode_api.application.mypage.service.MypageClassService;
import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageClassController {

    private final MypageClassService mypageClassService;

    @GetMapping("/detail")
    public ResponseEntity<?> getClassDetail(@RequestParam Long classId) {
        var response = mypageClassService.getClassDetail(classId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/classes")
    public ResponseEntity<?> getClasses(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // 서비스 호출
        return null;
    }
}
