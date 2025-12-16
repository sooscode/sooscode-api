package com.sooscode.sooscode_api.application.code.service;

import com.sooscode.sooscode_api.application.code.dto.ParticipantInfo;
import com.sooscode.sooscode_api.application.code.dto.ParticipantMessage;
import com.sooscode.sooscode_api.global.websocket.WebSocketSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipantsService {

    private final WebSocketSessionRegistry sessionRegistry;

    /**
     * 특정 클래스의 현재 참가자 목록 조회
     */
    public ParticipantMessage getParticipants(Long classId) {

        // 1classId에 속한 userId 목록 조회
        var userIds = sessionRegistry.getClassMembers(classId.toString());

        log.info("클래스 {} 참가자 수: {}", classId, userIds.size());

        List<ParticipantInfo> participant =
                userIds.stream()
                        .map(obj -> {
                            Long userId = ((Number) obj).longValue();

                            String sessionId =
                                    sessionRegistry.getExistingSessionId(userId);

                            boolean isInstructor =
                                    sessionId != null && sessionRegistry.isInstructor(sessionId);

                            return ParticipantInfo.builder()
                                    .userId(userId)
                                    .username("User #" + userId)
                                    .role(isInstructor ? "INSTRUCTOR" : "STUDENT")
                                    .isOnline(sessionId != null)
                                    .build();
                        })
                        .toList();

        return ParticipantMessage.builder()
                .classId(classId)
                .participant(participant)
                .build();
    }
}