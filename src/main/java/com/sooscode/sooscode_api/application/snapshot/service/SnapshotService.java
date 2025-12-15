package com.sooscode.sooscode_api.application.snapshot.service;

import com.sooscode.sooscode_api.application.snapshot.dto.SnapShotResponse;
import com.sooscode.sooscode_api.application.snapshot.dto.SnapshotLanguage;
import com.sooscode.sooscode_api.application.snapshot.dto.SnapshotRequest;
import com.sooscode.sooscode_api.application.snapshot.dto.SnapshotTitleResponse;
import com.sooscode.sooscode_api.domain.snapshot.entity.CodeSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface SnapshotService {
    // 코드 스냅샷 Id로 단일조회
    SnapShotResponse readSnapshot(Long userId, Long classId, Long snapshotId);
    // 스냅샷 저장하기
    CodeSnapshot saveCodeSnapshot(SnapshotRequest snapshotRequest, Long userId);
    // 스냅샷 업데이트
    CodeSnapshot updateCodeSnapshot(SnapshotRequest snapshotRequest, Long userId, Long snapshotId);
    // 스냅샷 전체조회
    Page<SnapShotResponse> readAllSnapshots(Long userId, Long classId, Pageable pageable);
    // 제목별로 조회
    List<SnapShotResponse> readSnapshotsByTitle(Long userId, Long classId, String title);
    // 내용별로 조회
    List<SnapShotResponse> readSnapshotsByContent(Long userId, Long classId, String content);
    // 날짜별로 조회
    List<SnapShotResponse> readSnapshotByDate(Long userId, Long classId, LocalDateTime start, LocalDateTime end);
    // 제목과 날짜별 조회
    List<SnapShotResponse> readSnapshotByTitleAndDate(Long userId, Long classId, String title,LocalDateTime start, LocalDateTime end);
    // 내용과 날짜별 조회
    List<SnapShotResponse> readSnapshotByContentAndDate(Long userId, Long classId, String content,LocalDateTime start, LocalDateTime end);
    // 날짜별 조회(제목만 로딩)
    List<SnapshotTitleResponse> readTitleByDate(Long userId, Long classId, LocalDateTime start, LocalDateTime end);
    // 특정 스냅샷 삭제
    void deleteSnapshot(Long userId, Long classId, Long snapshotId);
    // 언어 + 날짜별 조회
    Page<SnapshotTitleResponse> readSnapshotByLanguageAndDate(Long userId, Long classId, SnapshotLanguage language, LocalDateTime start, LocalDateTime end, Pageable pageable);
    // 제목 or 언어 or 날짜로 검색
    Page<SnapshotTitleResponse> searchSnapshotTitles(
            Long userId,
            Long classId,
            SnapshotLanguage language,
            String title,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );


}
