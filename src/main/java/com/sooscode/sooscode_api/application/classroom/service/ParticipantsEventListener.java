package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.ParticipantInfo;
import com.sooscode.sooscode_api.application.classroom.dto.ParticipantMessage;
import com.sooscode.sooscode_api.global.websocket.ParticipantsChangedEvent;
import com.sooscode.sooscode_api.global.websocket.WebSocketSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 참가자 변경 이벤트 리스너
 * - 참가자 입장/퇴장 시 브로드캐스트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ParticipantsEventListener {

    private final WebSocketSessionRegistry sessionRegistry;
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    @EventListener
    public void handleParticipantsChanged(ParticipantsChangedEvent event) {
        Long classId = event.getClassId();

        log.debug("참가자 변경 이벤트 수신: classId={}", classId);

        // 참가자 목록 조회
        ParticipantMessage message = getParticipants(classId);

        // 브로드캐스트
        String topic = "/topic/class/" + classId + "/participants";
        messagingTemplate.convertAndSend(topic, message);

        log.info("PARTICIPANTS BROADCAST → {}, count={}", topic, message.getParticipant().size());
    }

    /**
     * 특정 클래스의 현재 참가자 목록 조회
     */
    private ParticipantMessage getParticipants(Long classId) {
        var userIds = sessionRegistry.getClassMembers(classId.toString());

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