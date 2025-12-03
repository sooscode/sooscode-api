package com.sooscode.sooscode_api.application.classroom.controller;

import com.sooscode.sooscode_api.application.classroom.dto.ClassRoomCreateRequest;
import com.sooscode.sooscode_api.application.classroom.dto.ClassRoomResponse;
import com.sooscode.sooscode_api.application.classroom.service.ClassRoomService;
import com.sooscode.sooscode_api.application.snapshot.service.SnapshotService;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classroom")
@RequiredArgsConstructor
@Slf4j
public class ClassRoomController {

    private final ClassRoomService classRoomService;
    
    // 추후 Service에 @Service 선언 후 사용 예정
    // private final SnapshotService snapshotService;

    @GetMapping("/{classId}/detail")
    public ResponseEntity<?> getClassDetail(@PathVariable Long classId) {
        log.info("getClassDetail 실행");

        ClassRoomResponse.Detail detail = classRoomService.getClassDetail(classId);

        return ResponseEntity.ok(detail);
    }

    @PostMapping
    public ResponseEntity<?> createClassRoom(@RequestBody ClassRoomCreateRequest request) {
        log.info("createClassRoom Method");

        ClassRoom saved = classRoomService.createClassRoom(request);

        return ResponseEntity.ok(saved);
    }

    // 추후 Snapshot Service 작업 후 작성 예정
//    @GetMapping("/{classId}/snapshot/{userId}")
//    public ResponseEntity<?> getUserSnapshot(
//            @PathVariable Long classId,
//            @PathVariable Long userId
//    ) {
//        log.info("getUserSnapshot Method");
//
//        var snapshot = snapshotService.getUserSnapshot(userId, classId);
//
//        return ResponseEntity.ok(snapshot);
//    }


}
