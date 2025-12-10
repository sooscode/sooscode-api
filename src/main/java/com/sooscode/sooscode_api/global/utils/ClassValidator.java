package com.sooscode.sooscode_api.global.utils;

import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.ValidStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 클래스 입력 유효성 검증 유틸리티 클래스
 */
public class ClassValidator {

    /**
     * 클래스 제목 유효성 검증 (1~255자)
     * @throws CustomException
     */
    public static void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new CustomException(ValidStatus.CLASS_TITLE_REQUIRED);
        }

        String trimmed = title.trim();
        if (trimmed.length() < 1) {
            throw new CustomException(ValidStatus.CLASS_TITLE_TOO_SHORT);
        }
        if (trimmed.length() > 255) {
            throw new CustomException(ValidStatus.CLASS_TITLE_TOO_LONG);
        }
    }

    /**
     * 클래스 설명 유효성 검증 (최대 1000자)
     * @throws CustomException
     */
    public static void validateDescription(String description) {
        if (description == null) {
            return; // 설명은 선택사항
        }

        if (description.length() > 1000) {
            throw new CustomException(ValidStatus.CLASS_DESCRIPTION_TOO_LONG);
        }
    }

    /**
     * 클래스 시작/종료 시간 유효성 검증 (생성 시)
     * @throws CustomException
     */
    public static void validateClassTime(LocalDateTime startedAt, LocalDateTime endedAt) {
        // null 체크
        if (startedAt == null) {
            throw new CustomException(ValidStatus.CLASS_START_TIME_REQUIRED);
        }

        if (endedAt == null) {
            throw new CustomException(ValidStatus.CLASS_END_TIME_REQUIRED);
        }

        // 종료 시간이 시작 시간보다 빠르거나 같은 경우
        if (endedAt.isBefore(startedAt) || endedAt.isEqual(startedAt)) {
            throw new CustomException(ValidStatus.CLASS_END_TIME_BEFORE_START);
        }

        // 시작 시간이 과거인 경우 (생성 시에만 체크)
        if (startedAt.isBefore(LocalDateTime.now())) {
            throw new CustomException(ValidStatus.CLASS_START_TIME_PAST);
        }

        // 클래스 기간이 너무 짧은 경우 (최소 30분)
        if (endedAt.isBefore(startedAt.plusMinutes(30))) {
            throw new CustomException(ValidStatus.CLASS_DURATION_TOO_SHORT);
        }

        // 클래스 기간이 너무 긴 경우 (최대 24시간)
        if (endedAt.isAfter(startedAt.plusHours(24))) {
            throw new CustomException(ValidStatus.CLASS_DURATION_TOO_LONG);
        }
    }

    /**
     * 클래스 시간 수정 시 검증 (과거 시간 허용)
     * @throws CustomException
     */
    public static void validateClassTimeForUpdate(LocalDateTime startedAt, LocalDateTime endedAt) {
        // null 체크
        if (startedAt == null) {
            throw new CustomException(ValidStatus.CLASS_START_TIME_REQUIRED);
        }

        if (endedAt == null) {
            throw new CustomException(ValidStatus.CLASS_END_TIME_REQUIRED);
        }

        // 종료 시간이 시작 시간보다 빠르거나 같은 경우
        if (endedAt.isBefore(startedAt) || endedAt.isEqual(startedAt)) {
            throw new CustomException(ValidStatus.CLASS_END_TIME_BEFORE_START);
        }

        // 클래스 기간이 너무 짧은 경우 (최소 30분)
        if (endedAt.isBefore(startedAt.plusMinutes(30))) {
            throw new CustomException(ValidStatus.CLASS_DURATION_TOO_SHORT);
        }

        // 클래스 기간이 너무 긴 경우 (최대 24시간)
        if (endedAt.isAfter(startedAt.plusHours(24))) {
            throw new CustomException(ValidStatus.CLASS_DURATION_TOO_LONG);
        }
    }

    /**
     * 온라인 여부 유효성 검증
     * @throws CustomException
     */
    public static void validateIsOnline(Boolean isOnline) {
        if (isOnline == null) {
            throw new CustomException(ValidStatus.CLASS_IS_ONLINE_REQUIRED);
        }
    }

    /**
     * 강사 배정 검증
     * - 현재는 별도 검증 없음 → return
     */
    public static void validateAssignInstructor(Long instructorId) {
        if (instructorId == null || instructorId <= 0) {
            throw new CustomException(ValidStatus.CLASS_INSTRUCTOR_NOT_FOUND);
        }
    }

    /**
     * 학생 일괄 배정 검증
     * - 현재는 별도 검증 없음 → return
     */
    public static void validateAssignStudents(List<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            throw new CustomException(ValidStatus.CLASS_STUDENT_NOT_FOUND);
        }
    }

    /**
     * 클래스 생성 시 전체 데이터 검증
     * @throws CustomException
     */
    public static void validateCreateData(
            String title,
            String description,
            Boolean isOnline,
            LocalDateTime startedAt,
            LocalDateTime endedAt
    ) {
        validateTitle(title);
        validateDescription(description);
        validateIsOnline(isOnline);
        validateClassTime(startedAt, endedAt);
    }

    /**
     * 클래스 수정 시 전체 데이터 검증
     * @throws CustomException
     */
    public static void validateUpdateData(
            String title,
            String description,
            LocalDateTime startedAt,
            LocalDateTime endedAt
    ) {
        validateTitle(title);
        validateDescription(description);
        validateClassTimeForUpdate(startedAt, endedAt);
    }
}