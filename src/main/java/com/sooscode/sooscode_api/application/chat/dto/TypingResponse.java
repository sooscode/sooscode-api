package com.sooscode.sooscode_api.application.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TypingResponse {
    private Long classId;
    private Long userId;
    private String email;
    private String name;
    private boolean typing; // true=입력중, false=멈춤
}