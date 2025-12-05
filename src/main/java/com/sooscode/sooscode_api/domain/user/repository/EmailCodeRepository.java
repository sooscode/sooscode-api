package com.sooscode.sooscode_api.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sooscode.sooscode_api.domain.user.entity.EmailCode;
import java.util.Optional;

public interface EmailCodeRepository extends JpaRepository<EmailCode, Long> {
    /**
     * 해당 이메이르이 최신 인증 코드 1개 조회 (createdAt 기준 내림차순)
     */
    Optional<EmailCode> findTopByEmailOrderByEmailCodeIdDesc(String email);

    /**
     * 해당 이메일이 특정 인증 상태인지 여부 확인 (예: verified = true)
     */
    boolean existsByEmailAndIsVerified(String email, boolean isVerified);
}
