package com.sooscode.sooscode_api.domain.snapshot.repository;

import com.sooscode.sooscode_api.application.snapshot.dto.SnapshotTitleResponse;
import com.sooscode.sooscode_api.domain.snapshot.entity.CodeSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CodeSnapshotRepository extends JpaRepository<CodeSnapshot, Long> {
    // 전체조회 페이지네이션
    Page<CodeSnapshot> findAllByUser_UserIdAndClassRoom_ClassId(Long userId, Long classId , Pageable pageable);
    // 제목별로 조회
    List<CodeSnapshot> findByUser_UserIdAndClassRoom_ClassIdAndTitleContaining(Long userId, Long classId, String title);
    // 내용별로 조회
    List<CodeSnapshot> findByUser_UserIdAndClassRoom_ClassIdAndContentContaining(Long userId, Long classId, String content);
    // 날짜별로 조회
    List<CodeSnapshot> findByUser_userIdAndClassRoom_classIdAndCreatedAtBetween(Long userId, Long classId, LocalDateTime start, LocalDateTime end);
    // 제목과 날짜별 조회
    List<CodeSnapshot> findByUser_UserIdAndClassRoom_ClassIdAndTitleContainingAndCreatedAtBetween(Long userId, Long classId, String title, LocalDateTime start, LocalDateTime end);
    // 내용과 날짜별 조회
    List<CodeSnapshot> findByUser_UserIdAndClassRoom_ClassIdAndContentContainingAndCreatedAtBetween(Long userId, Long classId, String content, LocalDateTime start, LocalDateTime end);
    // 날짜별 조회(제목만 로딩)
    List<SnapshotTitleResponse> findByUser_UserIdAndClassRoom_ClassIdAndCreatedAtBetween(Long userId, Long classId, LocalDateTime start, LocalDateTime end);

}
