package com.sooscode.sooscode_api.domain.snapshot.repository;

import com.sooscode.sooscode_api.application.snapshot.dto.SnapshotLanguage;
import com.sooscode.sooscode_api.application.snapshot.dto.SnapshotTitleResponse;
import com.sooscode.sooscode_api.domain.snapshot.entity.CodeSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    // 특정 스냅샷 삭제
    long deleteByCodeSnapshotIdAndUser_UserIdAndClassRoom_ClassId(Long codeSnapshotId, Long userId, Long classId);
    //  언어 + classId + 시작/끝 날짜
    Page<CodeSnapshot> findByUser_UserIdAndClassRoom_ClassIdAndLanguageAndCreatedAtBetween(Long userId, Long classId, SnapshotLanguage language, LocalDateTime start, LocalDateTime end, Pageable pageable);
    // codeSnapshotId로 조회
    Optional<CodeSnapshot> findByCodeSnapshotIdAndUser_UserIdAndClassRoom_ClassId(Long codeSnapshotId, Long userUserId, Long classRoomClassId);
    // 제목 or 언어 or 날짜로 검색
    @Query("""
    SELECT new com.sooscode.sooscode_api.application.snapshot.dto.SnapshotTitleResponse(
        s.codeSnapshotId,
        s.title,
        s.language,
        s.createdAt
    )
    FROM CodeSnapshot s
    WHERE (:userId IS NULL OR s.user.userId = :userId)
      AND (:classId IS NULL OR s.classRoom.classId = :classId)
      AND (:language IS NULL OR s.language = :language)
      AND (:keyword IS NULL OR :keyword = '' OR LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (:start IS NULL OR s.createdAt >= :start)
      AND (:end IS NULL OR s.createdAt <= :end)
    ORDER BY s.createdAt DESC
""")
    Page<SnapshotTitleResponse> searchSnapshots(
            @Param("userId") Long userId,
            @Param("classId") Long classId,
            @Param("language") SnapshotLanguage language,
            @Param("keyword") String keyword,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

}
