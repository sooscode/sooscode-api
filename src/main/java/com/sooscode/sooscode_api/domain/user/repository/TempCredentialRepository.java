package com.sooscode.sooscode_api.domain.user.repository;

import com.sooscode.sooscode_api.domain.user.entity.TempCredential;
import com.sooscode.sooscode_api.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TempCredentialRepository extends JpaRepository<TempCredential, Long> {
    /**
     * 임시 비밀번호는 가장 최근(issued_at DESC)를 사용
     */
    Optional<TempCredential> findTopByUserOrderByCreatedAtDesc(User user);
}