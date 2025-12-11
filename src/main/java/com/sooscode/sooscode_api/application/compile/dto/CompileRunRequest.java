package com.sooscode.sooscode_api.application.compile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CompileRunRequest {

    private String jobId;
    private String code;
    private String callbackUrl;
}