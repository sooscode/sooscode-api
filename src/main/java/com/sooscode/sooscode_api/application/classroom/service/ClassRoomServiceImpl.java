package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.ClassRoomDetailResponse;
import com.sooscode.sooscode_api.application.code.service.CodeService;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassParticipantRepository;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.status.ClassRoomStatus;
import com.sooscode.sooscode_api.infra.websocket.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 클래스룸 관리 서비스
 * - 수업 종료
 * - 강제 퇴장
 * - 시스템 메시지 전송
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ClassRoomServiceImpl implements ClassRoomService {

    private final SessionService sessionService;
    private final ParticipantService participantService;
    private final CodeService codeService;
    private final SimpMessagingTemplate messagingTemplate;

    private final ClassRoomRepository classRoomRepository;
    private final ClassParticipantRepository classParticipantRepository;

    // 학생 입장 가능 시간 (시작 10분 전)
    private static final int EARLY_JOIN_MINUTES = 10;

    @Override
    public ClassRoomDetailResponse getClassRoomDetail(Long classId, Long userId) {

        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        if (!classRoom.isActive()) {
            throw new CustomException(ClassRoomStatus.CLASS_NOT_ACTIVE);
        }

        // 시간 검증 (userId 전달)
        validateClassJoinTime(classRoom, userId);

        boolean isInstructor = classRoom.getUser().getUserId().equals(userId);
        boolean isParticipant = classParticipantRepository
                .findByClassRoom_ClassIdAndUser_UserId(classId, userId)
                .isPresent();

        if (!isInstructor && !isParticipant) {
            throw new CustomException(ClassRoomStatus.CLASS_ACCESS_DENIED);
        }

        int participantCount = classParticipantRepository
                .countByClassRoom_ClassId(classId);

        int totalParticipantCount = participantCount + 1; // 강사 포함

        return ClassRoomDetailResponse.from(classRoom, totalParticipantCount, isInstructor);
    }

    /**
     * 수업 입장 시간 검증
     * - 강사: 언제든 입장 가능
     * - 학생: 시작 10분 전부터 종료 시간까지 입장 가능
     */
    private void validateClassJoinTime(ClassRoom classRoom, Long userId) {
        // 강사는 시간 제약 없음 (미리 준비 가능)
        boolean isInstructor = classRoom.getUser().getUserId().equals(userId);
        if (isInstructor) {
            log.debug("강사 입장: userId={}, 시간 제약 없음", userId);
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime classStart = LocalDateTime.of(classRoom.getStartDate(), classRoom.getStartTime());
        LocalDateTime classEnd = LocalDateTime.of(classRoom.getEndDate(), classRoom.getEndTime());

        // 학생은 시작 10분 전부터 입장 가능
        LocalDateTime earlyJoinTime = classStart.minusMinutes(EARLY_JOIN_MINUTES);

        if (now.isBefore(earlyJoinTime)) {
            log.warn("입장 시간 전: userId={}, 현재={}, 입장가능={}", userId, now, earlyJoinTime);
            throw new CustomException(ClassRoomStatus.CLASS_NOT_STARTED);
        }

        if (now.isAfter(classEnd)) {
            log.warn("수업 종료 후 입장 시도: userId={}, 현재={}, 종료={}", userId, now, classEnd);
            throw new CustomException(ClassRoomStatus.CLASS_ALREADY_ENDED);
        }

        log.debug("학생 입장 허용: userId={}, 현재={}", userId, now);
    }

    /**
     * 수업 종료
     * - 모든 참여자에게 종료 알림
     * - 모든 데이터 정리
     */
    @Override
    public void endClass(Long classId) {
        String classIdStr = String.valueOf(classId);

        // 1. 종료 메시지 전송
        SystemMessage message = new SystemMessage("CLASS_ENDED", "수업이 종료되었습니다.");
        messagingTemplate.convertAndSend("/topic/class/" + classId + "/system", message);

        // 2. 데이터 정리
        sessionService.clearClassMembers(classIdStr);
        participantService.clearAll(classIdStr);
        codeService.cleanup(classId);

        log.info("수업 종료: classId={}", classId);
    }

    /**
     * 특정 사용자 강제 퇴장
     */
    @Override
    public void kickUser(Long classId, Long userId, String reason) {
        String classIdStr = String.valueOf(classId);
        String kickReason = reason != null ? reason : "강사에 의해 퇴장되었습니다.";

        // 1. 퇴장 메시지 전송 (해당 유저에게만)
        SystemMessage message = new SystemMessage("KICKED", kickReason);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/system",
                message
        );

        // 2. 참여자 목록에서 제거
        participantService.leave(classIdStr, userId);

        log.info("사용자 강제 퇴장: classId={}, userId={}, reason={}", classId, userId, kickReason);
    }

    /**
     * 시스템 메시지 전송 (전체)
     */
    @Override
    public void sendSystemMessage(Long classId, String type, String content) {
        SystemMessage message = new SystemMessage(type, content);
        messagingTemplate.convertAndSend("/topic/class/" + classId + "/system", message);
    }

    /**
     * 접속자 수 조회
     */
    @Override
    public int getMemberCount(Long classId) {
        Set<Object> members = sessionService.getClassMembers(String.valueOf(classId));
        return members.size();
    }

    /**
     * 접속 여부 확인
     */
    @Override
    public boolean isMemberConnected(Long classId, Long userId) {
        return participantService.isParticipant(String.valueOf(classId), userId);
    }

    // ==================== 시스템 메시지 DTO ====================

    public record SystemMessage(String type, String message) {}
}