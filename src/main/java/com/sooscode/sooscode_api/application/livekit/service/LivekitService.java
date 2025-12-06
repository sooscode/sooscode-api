package com.sooscode.sooscode_api.application.livekit.service;

import com.sooscode.sooscode_api.application.livekit.dto.*;
import java.util.List;
import java.util.Map;

public interface LivekitService {

    String createToken(LivekitTokenRequest request, Long userId);

    LivekitRoomResponse createRoom(LivekitRoomRequest request);

    void endRoom(String roomName);

    List<LivekitParticipantResponse> getParticipants(String roomName);

    List<Map<String, Object>> getRooms();
}
