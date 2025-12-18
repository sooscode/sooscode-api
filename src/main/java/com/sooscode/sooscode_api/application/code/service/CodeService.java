package com.sooscode.sooscode_api.application.code.service;

import com.sooscode.sooscode_api.application.code.store.CodeRedisStore;
import com.sooscode.sooscode_api.application.code.dto.CodeData;
import com.sooscode.sooscode_api.application.code.dto.CodeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * 코드 공유 서비스
 * - Redis 저장 + WebSocket 브로드캐스트
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CodeService {

    private final CodeRedisStore codeRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 토픽 패턴
    private static final String INSTRUCTOR_TOPIC = "/topic/code/instructor/%d";
    private static final String STUDENT_TOPIC = "/topic/code/student/%d/%d";

    // ==================== 강사 코드 ====================

    /**
     * 강사 코드 공유
     * 1. Redis 저장
     * 2. 학생들에게 브로드캐스트
     */
    public void shareInstructorCode(Long classId, Long userId, String username, String code, String language) {
        // 1. Redis 저장
        CodeData data = CodeData.of(userId, code, language);
        codeRepository.saveInstructorCode(classId, data);

        // 2. 브로드캐스트
        CodeMessage message = CodeMessage.builder()
                .classId(classId)
                .userId(userId)
                .username(username)
                .code(code)
                .language(language)
                .instructor(true)
                .build();

        String topic = String.format(INSTRUCTOR_TOPIC, classId);
        messagingTemplate.convertAndSend(topic, message);

        log.debug("강사 코드 공유: classId={}, userId={}", classId, userId);
    }

    /**
     * 강사 코드 조회 (초기 로드용)
     */
    public CodeData getInstructorCode(Long classId) {
        return codeRepository.getInstructorCode(classId);
    }

    // ==================== 학생 코드 ====================

    /**
     * 학생 코드 공유
     * 1. Redis 저장
     * 2. 해당 학생을 구독 중인 강사에게 브로드캐스트
     */
    public void shareStudentCode(Long classId, Long userId, String username, String code, String language) {
        // 1. Redis 저장
        CodeData data = CodeData.of(userId, code, language);
        codeRepository.saveStudentCode(classId, userId, data);

        // 2. 해당 학생 전용 토픽으로 브로드캐스트
        CodeMessage message = CodeMessage.builder()
                .classId(classId)
                .userId(userId)
                .username(username)
                .code(code)
                .language(language)
                .instructor(false)
                .build();

        String topic = String.format(STUDENT_TOPIC, classId, userId);
        messagingTemplate.convertAndSend(topic, message);

        log.debug("학생 코드 공유: classId={}, userId={}", classId, userId);
    }

    /**
     * 학생 코드 조회 (초기 로드용)
     */
    public CodeData getStudentCode(Long classId, Long userId) {
        return codeRepository.getStudentCode(classId, userId);
    }

    // ==================== 강사가 학생 코드 수정 ====================

    /**
     * 강사가 학생 코드 수정
     * 1. Redis에 학생 코드로 저장
     * 2. 해당 학생 전용 토픽으로 브로드캐스트 (강사가 편집했음을 표시)
     */
    public void editStudentCodeByInstructor(
            Long classId,
            Long studentId,
            Long instructorId,
            String instructorName,
            String code,
            String language
    ) {
        // 1. Redis에 학생 코드로 저장 (소유권은 학생)
        CodeData data = CodeData.of(studentId, code, language);
        codeRepository.saveStudentCode(classId, studentId, data);

        // 2. 학생 전용 토픽으로 브로드캐스트 (editedByInstructor=true)
        CodeMessage message = CodeMessage.builder()
                .classId(classId)
                .userId(studentId)           // 코드 소유자는 학생
                .username(instructorName)    // 편집자는 강사
                .code(code)
                .language(language)
                .instructor(true)            // 편집자가 강사임
                .editedByInstructor(true)    // 강사가 편집했음을 명시
                .build();

        String topic = String.format(STUDENT_TOPIC, classId, studentId);
        messagingTemplate.convertAndSend(topic, message);

        log.debug("강사가 학생 코드 수정: classId={}, studentId={}, instructorId={}",
                classId, studentId, instructorId);
    }

    // ==================== 정리 ====================

    /**
     * 수업 종료 시 코드 데이터 정리
     */
    public void cleanup(Long classId) {
        codeRepository.deleteAllClassCodes(classId);
        log.info("코드 데이터 정리: classId={}", classId);
    }
}