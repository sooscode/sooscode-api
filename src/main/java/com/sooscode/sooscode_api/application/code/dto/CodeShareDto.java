// 3. CodeShareDto.java
package com.sooscode.sooscode_api.application.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeShareDto {
    private Long classId;
    private Long userId;
    private String userName;
    private String code;
}