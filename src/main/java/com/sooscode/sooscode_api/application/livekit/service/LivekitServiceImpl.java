package com.sooscode.sooscode_api.application.livekit.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sooscode.sooscode_api.application.livekit.dto.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LivekitServiceImpl implements LivekitService {

    @Value("${livekit.api-key}")
    private String apiKey;

    @Value("${livekit.api-secret}")
    private String apiSecret;

    @Value("${livekit.url}") // wss://xxxx.livekit.cloud
    private String serverUrl;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        log.info("Loaded LiveKit KEY = {}", apiKey);
        log.info("Loaded LiveKit SECRET = {}", apiSecret);
        log.info("Loaded LiveKit URL = {}", serverUrl);
    }


    /* =========================================================
       LiveKit REST API URL 변환
       ========================================================= */
    private String apiServerUrl() {
        return serverUrl
                .replace("wss://", "https://")
                .replace("ws://", "http://")
                + "/api";
    }


    /* =========================================================
       0. REST API 호출용 서버 JWT 생성
       ========================================================= */
    private String createServerAccessToken() {

        long now = System.currentTimeMillis() / 1000L;

        Map<String, Object> videoGrant = new HashMap<>();
        videoGrant.put("roomCreate", true);
        videoGrant.put("roomList", true);
        videoGrant.put("roomAdmin", true);

        SecretKey key = Keys.hmacShaKeyFor(apiSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setIssuer(apiKey)
                .setSubject("server")
                .claim("nbf", now - 10)
                .setExpiration(new Date((now + 3600) * 1000)) // 1 hour
                .claim("video", videoGrant)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    /* =========================================================
       1. Token 발급 (클라이언트용)
       ========================================================= */
    @Override
    public String createToken(LivekitTokenRequest request, Long userId) {

        log.info("createToken 실행: room={}, role={}, userId={}",
                request.getRoomName(), request.getRole(), userId);

        String identity = "user-" + userId;
        long now = System.currentTimeMillis() / 1000L;

        Map<String, Object> grant = new HashMap<>();
        grant.put("room", request.getRoomName());
        grant.put("roomJoin", true);

        if ("teacher".equals(request.getRole())) {
            grant.put("canPublish", true);
            grant.put("canSubscribe", true);
        } else {
            grant.put("canPublish", false);
            grant.put("canSubscribe", true);
        }

        SecretKey key = Keys.hmacShaKeyFor(apiSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setIssuer(apiKey)
                .setSubject(identity)
                .claim("nbf", now - 10)
                .setExpiration(new Date((now + 3600) * 1000))
                .claim("video", grant)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }



    /* =========================================================
       2. Room 생성 (핵심 수정 완료된 버전)
       ========================================================= */
    @Override
    public LivekitRoomResponse createRoom(LivekitRoomRequest request) {

        log.info("createRoom 실행: roomName={}", request.getRoomName());

        Map<String, Object> body = Map.of(
                "name", request.getRoomName(),
                "emptyTimeout", 300,       // 5분 유지
                "maxParticipants", 10
        );
        String raw = webClient.post()
                .uri(apiServerUrl() + "/rooms")
                .header("Authorization", "Bearer " + createServerAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN)   // ★ JSON 아님! text/plain!
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("LiveKit raw response = {}", raw);

        if (!"OK".equalsIgnoreCase(raw.trim())) {
            throw new RuntimeException("Room creation failed: " + raw);
        }

        return new LivekitRoomResponse(request.getRoomName());
    }




    /* =========================================================
       3. Room 종료
       ========================================================= */
    @Override
    public void endRoom(String roomName) {

        log.info("endRoom 실행: roomName={}", roomName);

        webClient.delete()
                .uri(apiServerUrl() + "/rooms/" + roomName)
                .header("Authorization", "Bearer " + createServerAccessToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }


    /* =========================================================
       4. 참가자 조회
       ========================================================= */
    @Override
    public List<LivekitParticipantResponse> getParticipants(String roomName) {

        log.info("getParticipants 실행: roomName={}", roomName);

        List<Map<String, Object>> response = webClient.get()
                .uri(apiServerUrl() + "/rooms/" + roomName + "/participants")
                .header("Authorization", "Bearer " + createServerAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();

        if (response == null) return List.of();

        return response.stream()
                .map(LivekitParticipantResponse::from)
                .toList();
    }

    @Override
    public List<Map<String, Object>> getRooms() {

        log.info("getRooms 실행");

        String raw = webClient.get()
                .uri(apiServerUrl() + "/rooms")
                .header("Authorization", "Bearer " + createServerAccessToken())
                .accept(MediaType.APPLICATION_JSON)   // ★ JSON으로 받아야 함
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("LiveKit rooms raw response = {}", raw);

        // 방이 없으면 "OK"
        if (raw == null || raw.trim().equalsIgnoreCase("OK")) {
            log.warn("LiveKit returned OK (no rooms)");
            return List.of();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(raw, new TypeReference<List<Map<String, Object>>>() {});
        }
        catch (Exception e) {
            log.error("Failed to parse LiveKit rooms response: {}", raw, e);
            throw new RuntimeException("Invalid rooms response from LiveKit", e);
        }
    }





}
