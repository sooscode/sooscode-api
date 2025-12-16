package com.sooscode.sooscode_api.domain.classroom.repository;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {
    // userId를 통해서 class의 강사 조회
    List<ClassRoom> findByUser_UserId(Long userId);

    @Query("""
    SELECT c
    FROM ClassRoom c
    LEFT JOIN c.user u
    WHERE (:keyword IS NULL OR :keyword = ''
           OR c.title LIKE CONCAT('%', :keyword, '%')
           OR (u IS NOT NULL AND u.name LIKE CONCAT('%', :keyword, '%')))
      AND (:startDate IS NULL OR c.startDate >= :startDate)
      AND (:endDate IS NULL OR c.endDate <= :endDate)
      AND c.isActive = true
    """)
    Page<ClassRoom> findByKeywordAndStatusAndDateRange(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
        SELECT c
        FROM ClassRoom c
        WHERE
            (
                c.lastClosedDate IS NULL
                OR c.lastClosedDate < :today
            )
            AND
            (
                c.endDate < :today
                OR (c.endDate = :today AND c.endTime <= :nowTime)
            )
        """)
    List<ClassRoom> findClassesToCleanup(
            @Param("today") LocalDate today,
            @Param("nowTime") LocalTime nowTime
    );


    /**
     * 페이징 없이 정렬만 적용한 클래스 목록 조회
     * Sort를 사용하여 정렬 기능 제공
     */
    @Query("""
        SELECT c
        FROM ClassRoom c
        LEFT JOIN c.user u
        WHERE (:keyword IS NULL OR :keyword = ''
               OR c.title LIKE CONCAT('%', :keyword, '%')
               OR (u IS NOT NULL AND u.name LIKE CONCAT('%', :keyword, '%')))
          AND (:startDate IS NULL OR c.startDate >= :startDate)
          AND (:endDate IS NULL OR c.endDate <= :endDate)
          AND c.isActive = true
        """)
    List<ClassRoom> findAllByKeywordAndStatusAndDateRange(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Sort sort
    );

    //mypage 진입시 리스팅
    Page<ClassRoom> findByUser_UserId(Long userId, Pageable pageable);

}