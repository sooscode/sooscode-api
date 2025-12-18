package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.store.ParticipantRedisStore;
import com.sooscode.sooscode_api.application.classroom.store.ParticipantsResponse;
import com.sooscode.sooscode_api.application.classroom.dto.ParticipantInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 참여자 관리 서비스
 * - 입장/퇴장 처리
 * - 참여자 목록 브로드캐스트
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRedisStore participantRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String PARTICIPANTS_TOPIC = "/topic/class/%s/participants";

    /**
     * 클래스 입장
     */
    public void join(String classId, Long userId, String username, boolean isInstructor) {
        participantRepository.save(classId, userId, username, isInstructor);
        broadcastParticipants(classId);
        log.info("참여자 입장: classId={}, userId={}, username={}, isInstructor={}",
                classId, userId, username, isInstructor);
    }

    /**
     * 클래스 퇴장
     */
    public void leave(String classId, Long userId) {
        participantRepository.delete(classId, userId);
        broadcastParticipants(classId);
        log.info("참여자 퇴장: classId={}, userId={}", classId, userId);
    }

    /**
     * 참여자 목록 브로드캐스트
     */
    public void broadcastParticipants(String classId) {
        List<ParticipantInfo> participants = participantRepository.findAll(classId);
        ParticipantsResponse response = ParticipantsResponse.of(classId, participants);

        String topic = String.format(PARTICIPANTS_TOPIC, classId);
        messagingTemplate.convertAndSend(topic, response);

        log.debug("참여자 브로드캐스트: classId={}, count={}", classId, participants.size());
    }

    /**
     * 참여자 목록 조회
     */
    public List<ParticipantInfo> getParticipants(String classId) {
        return participantRepository.findAll(classId);
    }

    /**
     * 학생 목록 조회
     */
    public List<ParticipantInfo> getStudents(String classId) {
        return participantRepository.findStudents(classId);
    }

    /**
     * 특정 참여자 조회
     */
    public ParticipantInfo getParticipant(String classId, Long userId) {
        return participantRepository.findOne(classId, userId);
    }

    /**
     * 강사 조회
     */
    public Optional<ParticipantInfo> getInstructor(String classId) {
        return participantRepository.findInstructor(classId);
    }

    /**
     * 참여자 수 조회
     */
    public long getCount(String classId) {
        return participantRepository.count(classId);
    }

    /**
     * 참여 여부 확인
     */
    public boolean isParticipant(String classId, Long userId) {
        return participantRepository.exists(classId, userId);
    }

    /**
     * 전체 삭제 (수업 종료 시)
     */
    public void clearAll(String classId) {
        participantRepository.deleteAll(classId);
        log.info("참여자 전체 삭제: classId={}", classId);
    }
}