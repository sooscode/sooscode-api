package com.sooscode.sooscode_api.application.livekit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class LivekitRoomResponse {

    private String roomName;

    public static LivekitRoomResponse of(Map<String, Object> map) {
        return new LivekitRoomResponse(
                map.get("name").toString()
        );
    }
}
