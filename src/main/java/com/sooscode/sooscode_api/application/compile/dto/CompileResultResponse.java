package com.sooscode.sooscode_api.application.compile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompileResultResponse {
    private String status;
    private String output;
}