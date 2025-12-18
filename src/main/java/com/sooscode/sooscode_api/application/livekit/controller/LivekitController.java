package com.sooscode.sooscode_api.application.livekit.controller;

import com.sooscode.sooscode_api.application.livekit.dto.LivekitRoomEndRequest;
import com.sooscode.sooscode_api.application.livekit.dto.LivekitRoomRequest;
import com.sooscode.sooscode_api.application.livekit.dto.LivekitTokenRequest;
import com.sooscode.sooscode_api.application.livekit.service.LivekitService;
import com.sooscode.sooscode_api.infra.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/livekit")
@RequiredArgsConstructor
@Slf4j
public class LivekitController {
    private final LivekitService livekitService;

    @Value("${LIVEKIT_URL}")
    private String wsUrl;

    @Value("${LIVEKIT_API_KEY}")
    private String apiKey;

    @Value("${LIVEKIT_API_SECRET}")
    private String apiSecret;

    @PostMapping("/token")
    public ResponseEntity<?> createToken(
            @RequestBody LivekitTokenRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        log.info("[LiveKit] createToken 요청 - userId={}, roomName={}",
                user.getUser().getUserId(), request.getRoomName());

        String token = livekitService.createToken(request, user.getUser().getUserId());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/room")
    public ResponseEntity<?> createRoom(@RequestBody LivekitRoomRequest request) {
        log.info("[LiveKit] createRoom 요청 - roomName={}", request.getRoomName());

        var response = livekitService.createRoom(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/room/end")
    public ResponseEntity<?> endRoom(@RequestBody LivekitRoomEndRequest request) {
        log.info("[LiveKit] endRoom 요청 - roomName={}", request.getRoomName());

        livekitService.endRoom(request.getRoomName());
        return ResponseEntity.ok(Map.of("ended", true));
    }

    @GetMapping("/room/{roomName}/participants")
    public ResponseEntity<?> listParticipants(@PathVariable String roomName) {
        log.info("[LiveKit] listParticipants 요청 - roomName={}", roomName);

        var list = livekitService.getParticipants(roomName);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/rooms")
    public ResponseEntity<?> getRooms() {
        log.info("[LiveKit] getRooms 요청");

        var rooms = livekitService.getRooms();
        return ResponseEntity.ok(rooms);
    }
}
