package com.sooscode.sooscode_api.application.livekit.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LivekitTokenRequest {

    // 어떤 방(Room)에 입장할 것인지
    private String roomName;

    // 학생/선생 권한 구분 → student / teacher
    private String role;
}
