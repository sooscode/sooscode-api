package com.sooscode.sooscode_api.application.snapshot.controller;

import com.sooscode.sooscode_api.application.snapshot.dto.SnapShotResponse;
import com.sooscode.sooscode_api.application.snapshot.dto.SnapshotRequest;
import com.sooscode.sooscode_api.application.snapshot.dto.SnapshotTitleResponse;
import com.sooscode.sooscode_api.application.snapshot.service.SnapshotService;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.errorcode.SnapshotErrorCode;
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
    public ResponseEntity<?> save(
            @RequestBody SnapshotRequest dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();

        writeEffectiveness(dto.getTitle(), dto.getContent());

        snapshotService.saveCodeSnapshot(dto, userId);
        return ResponseEntity.ok("스냅샷 저장 완료");
    }
    @PostMapping("/update")
    public ResponseEntity<?> update(
            @RequestBody SnapshotRequest dto,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long  snapshotId
    ){
        Long LoginuserId = userDetails.getUser().getUserId();

        writeEffectiveness(dto.getTitle(), dto.getContent());

        snapshotService.updateCodeSnapshot(dto,LoginuserId, snapshotId);
        return ResponseEntity.ok("스냅샷 수정 완료");



    }
    @GetMapping("/read")
    public ResponseEntity<Page<SnapShotResponse>> read(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        Long userId = userDetails.getUser().getUserId();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<SnapShotResponse> snapShotResponses =
                snapshotService.readAllSnapshots(userId, classId, pageable);

        pageReadEffectiveness(snapShotResponses);

        return ResponseEntity.ok(snapShotResponses);

    }
    @GetMapping("/read/title")
    public ResponseEntity<List<SnapShotResponse>> searchByTitle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long classId,
            @RequestParam String title
    ){
        Long userId = userDetails.getUser().getUserId();

        List<SnapShotResponse> snapShotResponses =
                snapshotService.readSnapshotsByTitle(userId, classId, title);

        listReadEffectiveness(snapShotResponses);

        return ResponseEntity.ok(snapShotResponses);

    }
    @GetMapping("/read/content")
    public ResponseEntity<List<SnapShotResponse>> searchByContent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long classId,
            @RequestParam String content
    ){
        Long userId = userDetails.getUser().getUserId();

        List<SnapShotResponse> snapShotResponses =
                snapshotService.readSnapshotsByContent(userId, classId, content);
        System.out.println(content);

        return ResponseEntity.ok(snapShotResponses);
    }
    @GetMapping("/read/date")
    public ResponseEntity<List<SnapShotResponse>> searchByDate(
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

        return ResponseEntity.ok(snapShotResponses);
    }
    @GetMapping("read/title/date")
    public ResponseEntity<List<SnapShotResponse>> searchByTitleAndDate(
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

        return ResponseEntity.ok(snapShotResponses);

    }
    @GetMapping("/read/content/date")
    public ResponseEntity<List<SnapShotResponse>> searchByContentAndDate(
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

        return ResponseEntity.ok(snapShotResponses);

    }
    @GetMapping("/read/onlytitle/date")
    public ResponseEntity<List<SnapshotTitleResponse>> searchOnlyTitleByDate(
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

        return ResponseEntity.ok(snapshotTitleResponses);
    }

    private void writeEffectiveness(String title,String content) {
        if (title == null || title.trim().isEmpty()) {
            throw new CustomException(SnapshotErrorCode.TITLE_EMPTY);
        }
        if (title.length() > 255) {
            throw new CustomException(SnapshotErrorCode.TITLE_TOO_LONG);
        }
        if (content == null || content.trim().isEmpty()) {
            throw new CustomException(SnapshotErrorCode.CONTENT_EMPTY);
        }
    }
    private void listReadEffectiveness(List result) {
        if (result == null || result.isEmpty()) {
            throw new CustomException(SnapshotErrorCode.LIST_EMPTY);
        }
    }
    private void pageReadEffectiveness(Page result) {
        if (result == null || result.isEmpty()) {
            throw new CustomException(SnapshotErrorCode.LIST_EMPTY);
        }
    }


}
