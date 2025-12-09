package com.sooscode.sooscode_api.application.mypage.controller;

import com.sooscode.sooscode_api.application.mypage.dto.ClassRoomFileResponse;
import com.sooscode.sooscode_api.application.mypage.dto.MypageClassFileDeleteRequest;
import com.sooscode.sooscode_api.application.mypage.dto.MypageClassFileUploadRequest;
import com.sooscode.sooscode_api.application.mypage.service.MypageClassFileService;
import com.sooscode.sooscode_api.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/classroom")
@RequiredArgsConstructor
@Slf4j
public class MypageClassFileController {

    private final MypageClassFileService mypageClassFileService;

    /**
     * 1) 클래스 자료 업로드 (DTO 기반)
     */
    @PostMapping("/files/upload")
    public ResponseEntity<?> uploadClassFiles(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            MypageClassFileUploadRequest rq
    ) throws Exception {

        rq.setTeacherId(userDetails.getUser().getUserId());

        log.info("uploadClassFiles Controller | classId={}, teacherId={}, date={}, fileCount={}",
                rq.getClassId(), rq.getTeacherId(), rq.getLectureDate(), rq.getFiles().size());

        List<ClassRoomFileResponse> response = mypageClassFileService.uploadFiles(rq);

        return ResponseEntity.ok(response);
    }


    /**
     * 2) 클래스 자료 전체 조회
     */
    @GetMapping("/{classId}/files")
    public ResponseEntity<?> getClassFiles(
            @PathVariable Long classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        var response = mypageClassFileService.getFilesByClassId(classId, pageable);

        return ResponseEntity.ok(response);
    }


    /**
     * 3) 특정 날짜 자료 조회
     */
    @GetMapping("/{classId}/files/by-date")
    public ResponseEntity<?> getFilesByLectureDate(
            @PathVariable Long classId,
            @RequestParam("lectureDate") String lectureDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        LocalDate date;
        try {
            date = LocalDate.parse(lectureDate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid date format. Expected yyyy-MM-dd");
        }

        Pageable pageable = PageRequest.of(page, size);

        var response = mypageClassFileService.getFilesByLectureDate(classId, date, pageable);

        return ResponseEntity.ok(response);
    }

    /**
     * 4) 파일 삭제 (다중 삭제)
     */
    @DeleteMapping("/files/batch")
    public ResponseEntity<?> deleteClassFiles(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MypageClassFileDeleteRequest rq
    ) throws Exception {

        rq.setTeacherId(userDetails.getUser().getUserId());

        mypageClassFileService.deleteFiles(rq);

        return ResponseEntity.ok("Files deleted successfully");
    }
}
