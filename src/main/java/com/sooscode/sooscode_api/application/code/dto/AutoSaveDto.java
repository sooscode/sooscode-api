package com.sooscode.sooscode_api.application.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoSaveDto {
    private Long classId;
    private Long userId;
    private String language;
    private String code;
    private String output;
    private LocalDateTime savedAt;

    public static AutoSaveDto from(CodeShareDto dto) {
        return AutoSaveDto.builder()
                .classId(dto.getClassId())
                .userId(dto.getUserId())
                .language(dto.getLanguage())
                .code(dto.getCode())
                .output(dto.getOutput())
                .savedAt(LocalDateTime.now())
                .build();
    }
}