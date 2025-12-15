package com.sooscode.sooscode_api.domain.classroom.repository;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassParticipantRepository extends JpaRepository<ClassParticipant, Long> {
    // classId를 전달받아서 Participant를 List로 반환하는 Repo
    List<ClassParticipant> findByClassRoom_ClassId(Long classId);

    // classId와 userId를 조합해서 Participant를 찾아서 반환
    Optional<ClassParticipant> findByClassRoom_ClassIdAndUser_UserId(Long classId, Long userId);

    // class에 참가하고있는 user를 조회
    List<ClassParticipant> findByUser_UserId(Long userId);

    // class에 참가하고 있는 학생 수 조회
    int countByClassRoom_ClassId(Long classId);

    /**
     * 클래스 학생 목록 조회 (페이징, 키워드 검색)
     * 키워드로 학생 이름 또는 이메일 검색
     */
    @Query("SELECT cp FROM ClassParticipant cp " +
            "JOIN FETCH cp.user u " +
            "WHERE cp.classRoom.classId = :classId " +
            "AND (:keyword IS NULL OR " +
            "     u.name LIKE %:keyword% OR " +
            "     u.email LIKE %:keyword%)")
    Page<ClassParticipant> findByClassIdWithKeyword(
            @Param("classId") Long classId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}