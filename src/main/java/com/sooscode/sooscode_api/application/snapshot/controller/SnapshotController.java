package com.sooscode.sooscode_api.application.snapshot.controller;

import com.sooscode.sooscode_api.application.snapshot.dto.*;
import com.sooscode.sooscode_api.application.snapshot.service.SnapshotService;
import com.sooscode.sooscode_api.domain.snapshot.entity.CodeSnapshot;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.response.ApiResponse;
import com.sooscode.sooscode_api.global.api.status.ClassRoomStatus;
import com.sooscode.sooscode_api.global.api.status.SnapshotStatus;
import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequestMapping("/api/snapshot")
@RequiredArgsConstructor
@Controller
public class SnapshotController {

    private final SnapshotService snapshotService;

    @PostMapping("/")
    public ResponseEntity<ApiResponse<SnapshotSaveResponse>> save(
            @RequestBody SnapshotRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();

        writeEffectiveness(request.getTitle(), request.getContent());
        classEffectiveness(request.getClassId());
        languageEffectiveness(request.getLanguage());
        CodeSnapshot savedSnapshot =
                snapshotService.saveCodeSnapshot(request, userId);

        return ApiResponse.ok(
                SnapshotStatus.OK,
                SnapshotSaveResponse.from(savedSnapshot)
        );
    }
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<Void>> update(
            @RequestBody SnapshotRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long snapshotId
    ){
        Long LoginuserId = userDetails.getUser().getUserId();

        writeEffectiveness(request.getTitle(), request.getContent());
        classEffectiveness(request.getClassId());
        snapshotService.updateCodeSnapshot(request,LoginuserId, snapshotId);
        return ApiResponse.ok(SnapshotStatus.UPDATE_OK);



    }
    @GetMapping("/read/each")
    public ResponseEntity<ApiResponse<SnapShotResponse>> readEach(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long classId,
            @RequestParam Long  snapshotId
    ){
        Long userId = userDetails.getUser().getUserId();
        classEffectiveness(classId);
        SnapShotResponse  snapShotResponse =  snapshotService.readSnapshot(userId, classId, snapshotId);
        return ApiResponse.ok(SnapshotStatus.READ_OK, snapShotResponse);
    }
    @GetMapping("/read")
    public ResponseEntity<ApiResponse<Page<SnapShotResponse>>> read(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        Long userId = userDetails.getUser().getUserId();
        classEffectiveness(classId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<SnapShotResponse> snapShotResponses =
                snapshotService.readAllSnapshots(userId, classId, pageable);

        return ApiResponse.ok(SnapshotStatus.READ_OK,snapShotResponses);
    }
    @GetMapping("/read/title")
    public ResponseEntity<ApiResponse<List<SnapShotResponse>>> searchByTitle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long classId,
            @RequestParam String title
    ){
        Long userId = userDetails.getUser().getUserId();

        List<SnapShotResponse> snapShotResponses =
                snapshotService.readSnapshotsByTitle(userId, classId, title);

        listReadEffectiveness(snapShotResponses);

        return ApiResponse.ok(SnapshotStatus.READ_OK,snapShotResponses);

    }
    @GetMapping("/read/content")
    public ResponseEntity<ApiResponse<List<SnapShotResponse>>> searchByContent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long classId,
            @RequestParam String content
    ){
        Long userId = userDetails.getUser().getUserId();

        List<SnapShotResponse> snapShotResponses =
                snapshotService.readSnapshotsByContent(userId, classId, content);
        System.out.println(content);

        return ApiResponse.ok(SnapshotStatus.READ_OK,snapShotResponses);
    }
    @GetMapping("/read/date")
    public ResponseEntity<ApiResponse<List<SnapShotResponse>>> searchByDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long classId,
            @RequestParam(required = false) String day,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ){
        Long userId = userDetails.getUser().getUserId();

        LocalDateTime start;
        LocalDateTime end;
        if(day != null) {
            LocalDate localDate = LocalDate.parse(day);
            start = localDate.atStartOfDay();
            end = localDate.atTime(LocalTime.MAX);
        }else{
            if (startDate == null || endDate == null) {
                throw new CustomException(SnapshotStatus.DATE_EMPTY);
            }
            start = LocalDate.parse(startDate).atStartOfDay();
            end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
        }
        List<SnapShotResponse> snapShotResponses =
                snapshotService.readSnapshotByDate(userId, classId, start, end);

        listReadEffectiveness(snapShotResponses);

        return ApiResponse.ok(SnapshotStatus.READ_OK,snapShotResponses);
    }
    @GetMapping("read/title/date")
    public ResponseEntity<ApiResponse<List<SnapShotResponse>>> searchByTitleAndDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long classId,
            @RequestParam String title,
            @RequestParam(required = false) String day,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ){
        Long userId = userDetails.getUser().getUserId();

        LocalDateTime start;
        LocalDateTime end;
        if(day != null) {
            LocalDate localDate = LocalDate.parse(day);
            start = localDate.atStartOfDay();
            end = localDate.atTime(LocalTime.MAX);
        }else{
            if (startDate == null || endDate == null) {
                throw new CustomException(SnapshotStatus.DATE_EMPTY);
            }
            start = LocalDate.parse(startDate).atStartOfDay();
            end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
        }

        List<SnapShotResponse> snapShotResponses =
                snapshotService.readSnapshotByTitleAndDate(userId, classId, title, start, end);

        listReadEffectiveness(snapShotResponses);

        return ApiResponse.ok(SnapshotStatus.READ_OK,snapShotResponses);

    }
    @GetMapping("/read/content/date")
    public ResponseEntity<ApiResponse<List<SnapShotResponse>>> searchByContentAndDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long classId,
            @RequestParam String content,
            @RequestParam(required = false) String day, // 당일조회용
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ){
        Long userId = userDetails.getUser().getUserId();

        LocalDateTime start;
        LocalDateTime end;
        if(day != null) {
            LocalDate localDate = LocalDate.parse(day);
            start = localDate.atStartOfDay();
            end = localDate.atTime(LocalTime.MAX);
        }else{
            if (startDate == null || endDate == null) {
                throw new CustomException(SnapshotStatus.DATE_EMPTY);
            }
            start = LocalDate.parse(startDate).atStartOfDay();
            end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
        }

        List<SnapShotResponse> snapShotResponses =
                snapshotService.readSnapshotByContentAndDate(userId, classId, content, start, end);
        listReadEffectiveness(snapShotResponses);

        return ApiResponse.ok(SnapshotStatus.READ_OK,snapShotResponses);

    }
    @GetMapping("/read/onlytitle/date")
    public ResponseEntity<ApiResponse<List<SnapshotTitleResponse>>> searchOnlyTitleByDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long classId,
            @RequestParam(required = false) String day,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ){
        Long userId = userDetails.getUser().getUserId();

        LocalDateTime start;
        LocalDateTime end;
        if(day != null) {
            LocalDate localDate = LocalDate.parse(day);
            start = localDate.atStartOfDay();
            end = localDate.atTime(LocalTime.MAX);
        }else{
            if (startDate == null || endDate == null) {
                throw new CustomException(SnapshotStatus.DATE_EMPTY);
            }
            start = LocalDate.parse(startDate).atStartOfDay();
            end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
        }

        List<SnapshotTitleResponse> snapshotTitleResponses =
                snapshotService.readTitleByDate(userId, classId, start, end);

        listReadEffectiveness(snapshotTitleResponses);

        return ApiResponse.ok(SnapshotStatus.READ_OK,snapshotTitleResponses);
    }
    @GetMapping("/read/language/date")
    public ResponseEntity<ApiResponse<Page<SnapshotTitleResponse>>> searchByLanguageAndDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam String language,
            @RequestParam Long classId,
            @RequestParam(required = false) String day,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        SnapshotLanguage snapshotLanguage;
        try {
            snapshotLanguage = SnapshotLanguage.valueOf(language.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(SnapshotStatus.LANGUAGE_EMPTY);
        }
        Long userId = userDetails.getUser().getUserId();

        LocalDateTime start;
        LocalDateTime end;
        if(day != null) {
            LocalDate localDate = LocalDate.parse(day);
            start = localDate.atStartOfDay();
            end = localDate.atTime(LocalTime.MAX);
        }else{
            if (startDate == null || endDate == null) {
                throw new CustomException(SnapshotStatus.DATE_EMPTY);
            }
            start = LocalDate.parse(startDate).atStartOfDay();
            end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
        }
        Page<SnapshotTitleResponse> responses =
                snapshotService.readSnapshotByLanguageAndDate(
                        userId, classId, snapshotLanguage, start, end, pageable
                );

        return ApiResponse.ok(SnapshotStatus.READ_OK, responses);
    }
    @GetMapping("/read/title/language/date")
    public ResponseEntity<ApiResponse<Page<SnapshotTitleResponse>>> searchByLanguageOrDateOrtitle(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long classId,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String day,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable

    ){
        Long userId = customUserDetails.getUser().getUserId();
        SnapshotLanguage snapshotLanguage = null;
        if (language != null && !language.isBlank()) {
            try {
                snapshotLanguage = SnapshotLanguage.valueOf(language.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CustomException(SnapshotStatus.LANGUAGE_EMPTY);
            }
        }
        classEffectiveness(classId);

        LocalDateTime start = null;
        LocalDateTime end = null;

        if (day != null && !day.isBlank()) {
            LocalDate localDate = LocalDate.parse(day);
            start = localDate.atStartOfDay();
            end = localDate.atTime(LocalTime.MAX);
        } else if (startDate != null && endDate != null) {
            start = LocalDate.parse(startDate).atStartOfDay();
            end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
        }

        Page<SnapshotTitleResponse> response =
                snapshotService.searchSnapshotTitles(
                        userId, classId, snapshotLanguage, title, start, end, pageable
                );
        return ApiResponse.ok(SnapshotStatus.READ_OK, response);

    }


    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteSnapshot(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long classId,
            @RequestParam Long snapshotId
    ) {
        Long userId = userDetails.getUser().getUserId();
        snapshotService.deleteSnapshot(userId, classId, snapshotId);
        return ApiResponse.ok(SnapshotStatus.DELETE_OK);
    }

    private void classEffectiveness(Long classId){
        if(classId==null){
            throw new CustomException(ClassRoomStatus.CLASS_NOT_FOUND);
        }
    }
    private void languageEffectiveness(SnapshotLanguage language) {
        if (language == null) {
            throw new CustomException(SnapshotStatus.LANGUAGE_EMPTY);

        }
    }

    private void writeEffectiveness(String title,String content) {
        if (title == null || title.trim().isEmpty()) {
            throw new CustomException(SnapshotStatus.TITLE_EMPTY);
        }
        if (title.length() > 255) {
            throw new CustomException(SnapshotStatus.TITLE_TOO_LONG);
        }
        if (content == null || content.trim().isEmpty()) {
            throw new CustomException(SnapshotStatus.CONTENT_EMPTY);
        }
    }
    private void listReadEffectiveness(List result) {
        if (result == null || result.isEmpty()) {
            throw new CustomException(SnapshotStatus.LIST_EMPTY);
        }
    }

}
