package com.sooscode.sooscode_api.application.mypage.controller;

import com.sooscode.sooscode_api.application.mypage.service.MypageClassService;
import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<?> getClassDetail(@PathVariable Long classId) {
        var response = mypageClassService.getClassDetail(classId);
        return ResponseEntity.ok(response);
    }

    // Instructor 및 student에 따라 가지고있는 class의 List를 반환
    @GetMapping("/classes")
    public ResponseEntity<?> getClasses(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Get My Classes Controller");

        Object response;
        Long userId = userDetails.getUser().getUserId();

        String userRole = userDetails.getUser().getRole().toString();

        log.info("User Role : {}", userRole);

        if(userRole.equals("STUDENT")){
            response = mypageClassService.getStudentClasses(userId);
        }else if(userRole.equals("INSTRUCTOR")){
            response = mypageClassService.getTeacherClasses(userId);
        }else {
            return ResponseEntity.badRequest().body("Invalid Role");
        }

        return ResponseEntity.ok(response);
    }
}
