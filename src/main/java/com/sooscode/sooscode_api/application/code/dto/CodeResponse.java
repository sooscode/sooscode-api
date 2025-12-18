package com.sooscode.sooscode_api.application.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeResponse {

    private Long userId;
    private String code;
    private String language;
    private boolean empty;

    public static CodeResponse of(CodeData data) {
        return CodeResponse.builder()
                .userId(data.getUserId())
                .code(data.getCode())
                .language(data.getLanguage())
                .empty(false)
                .build();
    }

    public static CodeResponse empty() {
        return CodeResponse.builder()
                .empty(true)
                .build();
    }
}