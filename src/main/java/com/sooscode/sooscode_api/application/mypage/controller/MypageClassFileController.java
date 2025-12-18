package com.sooscode.sooscode_api.application.mypage.controller;

import com.sooscode.sooscode_api.application.mypage.dto.MypageClassFileResponse;
import com.sooscode.sooscode_api.application.mypage.dto.MypageClassFileDeleteRequest;
import com.sooscode.sooscode_api.application.mypage.dto.MypageClassFileUploadRequest;
import com.sooscode.sooscode_api.application.mypage.service.MypageClassFileService;
import com.sooscode.sooscode_api.global.response.ApiResponse;
import com.sooscode.sooscode_api.global.status.GlobalStatus;
import com.sooscode.sooscode_api.infra.security.CustomUserDetails;

import com.sooscode.sooscode_api.global.utils.FileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Slf4j
public class MypageClassFileController {

    private final MypageClassFileService mypageClassFileService;

    /**
     * 1) 클래스 자료 업로드 (DTO 기반)
     */
    @PostMapping("/files/upload")
    public ResponseEntity<ApiResponse<List<MypageClassFileResponse>>> uploadClassFiles(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            MypageClassFileUploadRequest request
    ) throws Exception {

        log.info("[MypageFile] uploadClassFiles 요청 - classId={}",
                request.getClassId());

        // DTO에 TeacherId setting
        request.setTeacherId(userDetails.getUser().getUserId());

        log.info("[MypageFile] uploadClassFiles teacher 설정 - teacherId={}",
                request.getTeacherId());

        // file null 체크, 갯수(10개) 이하 체크, 날짜형식 체크, 확장자, 사이즈 체크
        FileValidator.validateUploadData(
                request.getLectureDate(),
                request.getFiles()
        );

        // 1) 날짜 파싱 2) 날짜 형식 검증 3) 날짜 null 체크
        LocalDate date = FileValidator.validateAndParseLectureDate(request.getLectureDate());

        List<MypageClassFileResponse> response = mypageClassFileService.uploadFiles(request);

        log.info("[MypageFile] uploadClassFiles 완료 - classId={}, fileCount={}",
                request.getClassId(),
                response.size());

        return ApiResponse.ok(GlobalStatus.OK, response);
    }

    /**
     * 2) 클래스 자료 전체 조회
     */
    @GetMapping("/{classId}/files")
    public ResponseEntity<ApiResponse<Page<MypageClassFileResponse>>> getClassFiles(
            @PathVariable Long classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("[MypageFile] getClassFiles 요청 - classId={}, page={}, size={}",
                classId, page, size);

        Pageable pageable = PageRequest.of(page, size);

        var response = mypageClassFileService.getFilesByClassId(classId, pageable);

        return ApiResponse.ok(GlobalStatus.OK, response);
    }

    /**
     * 3) 특정 날짜 자료 조회
     */
    @GetMapping("/{classId}/files/by-date")
    public ResponseEntity<ApiResponse<Page<MypageClassFileResponse>>> getFilesByLectureDate(
            @PathVariable Long classId,
            @RequestParam("lectureDate") String lectureDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("[MypageFile] getFilesByLectureDate 요청 - classId={}, lectureDate={}, page={}, size={}",
                classId, lectureDate, page, size);

        // 날짜 검증
        LocalDate date = FileValidator.validateAndParseLectureDate(lectureDate);

        Pageable pageable = PageRequest.of(page, size);

        Page<MypageClassFileResponse> response =
                mypageClassFileService.getFilesByLectureDate(classId, date, pageable);

        return ApiResponse.ok(GlobalStatus.OK, response);
    }

    /**
     * 4) 파일 삭제 (다중 삭제)
     */
    @DeleteMapping("/files/batch")
    public ResponseEntity<ApiResponse<Void>> deleteClassFiles(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MypageClassFileDeleteRequest request
    ) throws Exception {

        log.info("[MypageFile] deleteClassFiles 요청 - fileIds={}",
                request.getFileIds());

        request.setTeacherId(userDetails.getUser().getUserId());

        log.info("[MypageFile] deleteClassFiles teacher 설정 - teacherId={}",
                request.getTeacherId());

        // fileId 리스트 검증
        FileValidator.validateDeleteFileIds(request.getFileIds());

        mypageClassFileService.deleteFiles(request);

        log.info("[MypageFile] deleteClassFiles 완료");

        return ApiResponse.ok(GlobalStatus.OK);
    }
}
