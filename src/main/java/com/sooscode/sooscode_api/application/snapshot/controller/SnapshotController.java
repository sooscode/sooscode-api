package com.sooscode.sooscode_api.application.snapshot.controller;

import com.sooscode.sooscode_api.application.snapshot.dto.SnapShotResponse;
import com.sooscode.sooscode_api.application.snapshot.dto.SnapshotRequest;
import com.sooscode.sooscode_api.application.snapshot.dto.SnapshotTitleResponse;
import com.sooscode.sooscode_api.application.snapshot.service.SnapshotService;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.response.ApiResponse;
import com.sooscode.sooscode_api.global.api.status.SnapshotStatus;
import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<ApiResponse<Void>> save(
            @RequestBody SnapshotRequest dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();

        writeEffectiveness(dto.getTitle(), dto.getContent());

        snapshotService.saveCodeSnapshot(dto, userId);
        return ApiResponse.ok(SnapshotStatus.OK);
    }
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<Void>> update(
            @RequestBody SnapshotRequest dto,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long  snapshotId
    ){
        Long LoginuserId = userDetails.getUser().getUserId();

        writeEffectiveness(dto.getTitle(), dto.getContent());

        snapshotService.updateCodeSnapshot(dto,LoginuserId, snapshotId);
        return ApiResponse.ok(SnapshotStatus.UPDATE_OK);



    }
    @GetMapping("/read")
    public ResponseEntity<ApiResponse<Page<SnapShotResponse>>> read(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        Long userId = userDetails.getUser().getUserId();

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
            @RequestParam String day
    ){
        Long userId = userDetails.getUser().getUserId();

        LocalDate localDate = LocalDate.parse(day);

        LocalDateTime start = localDate.atStartOfDay();
        LocalDateTime end = localDate.atTime(LocalTime.MAX);

        List<SnapShotResponse> snapShotResponses =
                snapshotService.readSnapshotByDate(userId, classId, start, end);

        listReadEffectiveness(snapShotResponses);

        return ApiResponse.ok(SnapshotStatus.READ_OK,snapShotResponses);
    }
    @GetMapping("read/title/date")
    public ResponseEntity<ApiResponse<List<SnapShotResponse>>> searchByTitleAndDate(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long classId,
            @RequestParam String title,
            @RequestParam String day
    ){
        Long userId = customUserDetails.getUser().getUserId();

        LocalDate localDate = LocalDate.parse(day);

        LocalDateTime start = localDate.atStartOfDay();
        LocalDateTime end = localDate.atTime(LocalTime.MAX);

        List<SnapShotResponse> snapShotResponses =
                snapshotService.readSnapshotByTitleAndDate(userId, classId, title, start, end);

        listReadEffectiveness(snapShotResponses);

        return ApiResponse.ok(SnapshotStatus.READ_OK,snapShotResponses);

    }
    @GetMapping("/read/content/date")
    public ResponseEntity<ApiResponse<List<SnapShotResponse>>> searchByContentAndDate(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Long classId,
            @RequestParam String content,
            @RequestParam String day
    ){
        Long userId = customUserDetails.getUser().getUserId();

        LocalDate localDate = LocalDate.parse(day);

        LocalDateTime start = localDate.atStartOfDay();
        LocalDateTime end = localDate.atTime(LocalTime.MAX);

        List<SnapShotResponse> snapShotResponses =
                snapshotService.readSnapshotByContentAndDate(userId, classId, content, start, end);
        listReadEffectiveness(snapShotResponses);

        return ApiResponse.ok(SnapshotStatus.READ_OK,snapShotResponses);

    }
    @GetMapping("/read/onlytitle/date")
    public ResponseEntity<ApiResponse<List<SnapshotTitleResponse>>> searchOnlyTitleByDate(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam String day,
            @RequestParam Long classId
    ){
        Long userId = customUserDetails.getUser().getUserId();

        LocalDate localDate = LocalDate.parse(day);

        LocalDateTime start = localDate.atStartOfDay();
        LocalDateTime end = localDate.atTime(LocalTime.MAX);

        List<SnapshotTitleResponse> snapshotTitleResponses =
                snapshotService.readContentByDate(userId, classId, start, end);

        listReadEffectiveness(snapshotTitleResponses);

        return ApiResponse.ok(SnapshotStatus.READ_OK,snapshotTitleResponses);
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
