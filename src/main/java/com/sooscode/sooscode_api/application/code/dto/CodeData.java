package com.sooscode.sooscode_api.application.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 코드 데이터 DTO (Redis 저장용)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeData {

    private Long userId;
    private String code;
    private String language;
    private LocalDateTime updatedAt;

    public static CodeData of(Long userId, String code, String language) {
        return CodeData.builder()
                .userId(userId)
                .code(code)
                .language(language)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}