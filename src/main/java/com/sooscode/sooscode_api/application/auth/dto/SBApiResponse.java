package com.sooscode.sooscode_api.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SBApiResponse<T> {
    private boolean success;
    private String message;
    private T user;
}
