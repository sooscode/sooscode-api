package com.sooscode.sooscode_api.application.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeShareDto {
    private Long classId;
    private Long userId;
    private String userName;
    private String code;
    private String language;
    private Integer cursorPosition;
}