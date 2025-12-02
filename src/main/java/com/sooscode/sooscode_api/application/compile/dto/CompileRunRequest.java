package com.sooscode.sooscode_api.application.compile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CompileRunRequest {

    @NotBlank(message = "코드는 필수입니다.")
    @Size(max = 10000, message = "코드는 10,000자를 넘을 수 없습니다.")
    private String code;
}