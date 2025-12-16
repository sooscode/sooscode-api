package com.sooscode.sooscode_api.domain.user.repository;

import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회 (로그인/인증 시 사용)
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일 존재 여부 확인 (회원가입 중복 검사)
     */
    boolean existsByEmail(String email);

    /**
     * 이메일 존재 여부 + 상태 확인 (탈퇴 회원 재가입 검사)
     */
    Optional<User> findByEmailAndStatus(String email, UserStatus status);

    /**
     * 로그인 시에 프로필 이미지 불러오기
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.file WHERE u.email = :email")
    Optional<User> findByEmailWithFile(@Param("email") String email);

    /**
     * 사용자 목록 조회 (필터링 + 페이지네이션)
     * null인 파라미터는 필터에서 제외
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:role IS NULL OR u.role = :role) " +
            "AND (:status IS NULL OR u.status = :status) " +
            "AND (:startDate IS NULL OR u.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR u.createdAt <= :endDate)")
    Page<User> findByFilter(
            @Param("keyword") String keyword,
            @Param("role") UserRole role,
            @Param("status") UserStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    /**
     * 사용자 목록 조회 (필터링, 페이지네이션 없이 - 엑셀 다운로드용)
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:role IS NULL OR u.role = :role) " +
            "AND (:status IS NULL OR u.status = :status) " +
            "AND (:startDate IS NULL OR u.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR u.createdAt <= :endDate) " +
            "ORDER BY u.createdAt DESC")
    List<User> findByFilterAll(
            @Param("keyword") String keyword,
            @Param("role") UserRole role,
            @Param("status") UserStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 역할별 사용자 수 조회
     */
    long countByRole(UserRole role);

    /**
     * 상태별 사용자 수 조회
     */
    long countByStatus(UserStatus status);

    /**
     * 강사 검색 (이름 또는 이메일)
     * ACTIVE 상태의 INSTRUCTOR 역할만 조회
     */
    @Query("SELECT u FROM User u WHERE " +
            "u.role = 'INSTRUCTOR' " +
            "AND u.status = 'ACTIVE' " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> searchInstructors(@Param("keyword") String keyword);

    /**
     * 특정 클래스에 속하지 않은 학생 검색
     * ACTIVE 상태의 STUDENT 역할 중 해당 클래스에 참여하지 않은 학생만 조회
     */
    @Query("SELECT u FROM User u WHERE " +
            "u.role = 'STUDENT' " +
            "AND u.status = 'ACTIVE' " +
            "AND u.userId NOT IN (" +
            "   SELECT cp.user.userId FROM ClassParticipant cp " +
            "   WHERE cp.classRoom.classId = :classId" +
            ") " +
            "AND (:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> searchAvailableStudents(
            @Param("classId") Long classId,
            @Param("keyword") String keyword
    );
}