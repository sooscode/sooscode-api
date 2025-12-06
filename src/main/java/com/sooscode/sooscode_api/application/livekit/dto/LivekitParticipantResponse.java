package com.sooscode.sooscode_api.application.livekit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class LivekitParticipantResponse {

    private String identity;     // 사용자 식별자
    private boolean isPublisher; // 선생이면 true

    public static LivekitParticipantResponse from(Map<String, Object> raw) {
        return new LivekitParticipantResponse(
                raw.get("identity").toString(),
                Boolean.parseBoolean(raw.get("isPublisher").toString())
        );
    }
}
