package com.sooscode.sooscode_api.application.auth.service;


import com.sooscode.sooscode_api.domain.user.entity.EmailCode;
import com.sooscode.sooscode_api.domain.user.repository.EmailCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailCodeRepository emailCodeRepository;

    public boolean verify(String email, String code) {

        EmailCode dbCode = emailCodeRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new RuntimeException("코드를 찾을 수 없습니다."));

        if (dbCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("인증코드가 만료되었습니다.");
        }

        if (!dbCode.getCode().equals(code)) {
            return false;
        }

        dbCode.setVerified(true);
        emailCodeRepository.save(dbCode);

        return true;
    }
}
