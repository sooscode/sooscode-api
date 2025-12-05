package com.sooscode.sooscode_api.application.snapshot.controller;

import com.sooscode.sooscode_api.application.snapshot.dto.SnapShotResponse;
import com.sooscode.sooscode_api.application.snapshot.dto.SnapshotRequest;
import com.sooscode.sooscode_api.application.snapshot.service.SnapshotService;
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

        snapshotService.saveCodeSnapshot(dto, userId);
        return ResponseEntity.ok("스냅샷 저장 완료");
    }
    @GetMapping("/read")
    public ResponseEntity<Page<SnapShotResponse>> read(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam Long classId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        Long userId = userDetails.getUser().getUserId();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page <SnapShotResponse> snapShotResponses =
                snapshotService.readAllSnapshots(userId, classId, pageable);

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

        return ResponseEntity.ok(snapShotResponses);
    }


}
