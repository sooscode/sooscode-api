package com.sooscode.sooscode_api.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.sooscode.sooscode_api.domain.user.entity.EmailCode;
import java.util.Optional;

public interface EmailCodeRepository extends JpaRepository<EmailCode, Long> {

    Optional<EmailCode> findTopByEmailOrderByCreatedAtDesc(String email);
    boolean existsByEmailAndVerified(String email, boolean verified);
}
