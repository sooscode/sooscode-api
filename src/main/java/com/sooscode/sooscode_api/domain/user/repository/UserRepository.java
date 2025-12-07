package com.sooscode.sooscode_api.domain.user.repository;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sooscode.sooscode_api.domain.user.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

}
