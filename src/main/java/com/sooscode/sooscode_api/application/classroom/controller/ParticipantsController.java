package com.sooscode.sooscode_api.application.code.controller;

import com.sooscode.sooscode_api.application.classroom.dto.ParticipantMessage;
import com.sooscode.sooscode_api.application.classroom.service.ParticipantsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ParticipantsController {

    private final ParticipantsService participantsService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 참가자 목록 요청 및 브로드캐스트
     * - /app/class/{classId}/participants로 요청
     * - /topic/class/{classId}/participants로 응답
     */
    @MessageMapping("/class/{classId}/participants")
    public void getParticipants(@DestinationVariable Long classId) {
        try {
            ParticipantMessage participants = participantsService.getParticipants(classId);

            String topic = "/topic/class/" + classId + "/participants";
            messagingTemplate.convertAndSend(topic, participants);

            log.info("참가자 목록 전송 → {}, 인원: {}", topic, participants.getParticipant().size());
        } catch (Exception e) {
            log.error("참가자 목록 조회 실패: classId={}", classId, e);
        }
    }
}