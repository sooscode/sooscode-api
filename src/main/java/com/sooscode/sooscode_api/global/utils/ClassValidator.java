package com.sooscode.sooscode_api.global.utils;

import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.ValidStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 클래스 입력 유효성 검증 유틸리티 클래스
 */
public class ClassValidator {

    // ===== 텍스트 필드 검증 =====

    /**
     * 클래스 제목 유효성 검증
     * - 필수값
     * - 1자 이상 255자 이하
     *
     * @param title 클래스 제목
     * @throws CustomException 제목이 null, 빈값, 길이 초과 시
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
     * 클래스 설명 유효성 검증
     * - 선택값 (null 허용)
     * - 최대 1000자
     *
     * @param description 클래스 설명
     * @throws CustomException 길이 초과 시
     */
    public static void validateDescription(String description) {
        if (description == null) {
            return;
        }

        if (description.length() > 1000) {
            throw new CustomException(ValidStatus.CLASS_DESCRIPTION_TOO_LONG);
        }
    }

    // ===== 강의 운영 날짜 검증 =====

    /**
     * 강의 운영 날짜 검증
     * - 시작일, 종료일 필수
     * - 시작일은 오늘 이후여야 함 (과거 날짜 불가)
     * - 종료일은 시작일 이후여야 함
     *
     * 예: 12월 10일 ~ 12월 20일 동안 강의 운영
     *
     * @param startDate 강의 시작일
     * @param endDate 강의 종료일
     * @throws CustomException 날짜 조건 불만족 시
     */
    public static void validateClassDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new CustomException(ValidStatus.CLASS_START_DATE_REQUIRED);
        }
        if (endDate == null) {
            throw new CustomException(ValidStatus.CLASS_END_DATE_REQUIRED);
        }

        // 시작일이 오늘보다 과거면 불가
        if (startDate.isBefore(LocalDate.now())) {
            throw new CustomException(ValidStatus.CLASS_START_DATE_PAST);
        }

        // 종료일이 시작일보다 과거면 불가
        if (endDate.isBefore(startDate)) {
            throw new CustomException(ValidStatus.CLASS_END_DATE_BEFORE_START);
        }
    }

    /**
     * 강의 운영 날짜 검증 (수정용)
     * - 시작일 과거 허용 (이미 시작된 강의 수정 가능)
     * - 종료일은 시작일 이후여야 함
     *
     * @param startDate 강의 시작일
     * @param endDate 강의 종료일
     * @throws CustomException 날짜 조건 불만족 시
     */
    public static void validateClassDatesForUpdate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            throw new CustomException(ValidStatus.CLASS_START_DATE_REQUIRED);
        }
        if (endDate == null) {
            throw new CustomException(ValidStatus.CLASS_END_DATE_REQUIRED);
        }

        // 종료일이 시작일보다 과거면 불가
        if (endDate.isBefore(startDate)) {
            throw new CustomException(ValidStatus.CLASS_END_DATE_BEFORE_START);
        }
    }

    // ===== 강의 시간대 검증 =====

    /**
     * 강의 시간대 검증
     * - 시작 시간, 종료 시간 필수
     * - 종료 시간은 시작 시간 이후여야 함
     * - 최소 30분 이상
     * - 최대 12시간 이하
     *
     * 예: 매일 14:00 ~ 16:00에 강의 진행
     *
     * @param startTime 강의 시작 시간
     * @param endTime 강의 종료 시간
     * @throws CustomException 시간 조건 불만족 시
     */
    public static void validateClassTimes(LocalTime startTime, LocalTime endTime) {
        if (startTime == null) {
            throw new CustomException(ValidStatus.CLASS_START_TIME_REQUIRED);
        }
        if (endTime == null) {
            throw new CustomException(ValidStatus.CLASS_END_TIME_REQUIRED);
        }

        // 종료 시간이 시작 시간보다 빠르거나 같으면 불가
        if (!endTime.isAfter(startTime)) {
            throw new CustomException(ValidStatus.CLASS_END_TIME_BEFORE_START);
        }

        // 최소 30분 이상
        if (endTime.isBefore(startTime.plusMinutes(30))) {
            throw new CustomException(ValidStatus.CLASS_DURATION_TOO_SHORT);
        }

        // 최대 12시간 이하
        if (endTime.isAfter(startTime.plusHours(12))) {
            throw new CustomException(ValidStatus.CLASS_DURATION_TOO_LONG);
        }
    }

    // ===== 기타 필드 검증 =====

    /**
     * 온라인 여부 검증
     * - 필수값
     *
     * @param isOnline 온라인 여부
     * @throws CustomException null인 경우
     */
    public static void validateIsOnline(Boolean isOnline) {
        if (isOnline == null) {
            throw new CustomException(ValidStatus.CLASS_IS_ONLINE_REQUIRED);
        }
    }

    /**
     * 강사 ID 검증
     * - 필수값
     * - 양수여야 함
     *
     * @param instructorId 강사 ID
     * @throws CustomException null이거나 0 이하인 경우
     */
    public static void validateInstructorId(Long instructorId) {
        if (instructorId == null || instructorId <= 0) {
            throw new CustomException(ValidStatus.CLASS_INSTRUCTOR_NOT_FOUND);
        }
    }

    /**
     * 학생 ID 목록 검증
     * - 빈 목록 불가
     *
     * @param studentIds 학생 ID 목록
     * @throws CustomException null이거나 비어있는 경우
     */
    public static void validateStudentIds(List<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            throw new CustomException(ValidStatus.CLASS_STUDENT_NOT_FOUND);
        }
    }

    // ===== 통합 검증 =====

    /**
     * 클래스 생성 시 전체 검증
     *
     * @param title 클래스 제목
     * @param description 클래스 설명
     * @param isOnline 온라인 여부
     * @param startDate 강의 시작일
     * @param endDate 강의 종료일
     * @param startTime 강의 시작 시간
     * @param endTime 강의 종료 시간
     * @throws CustomException 검증 실패 시
     */
    public static void validateCreate(
            String title,
            String description,
            Boolean isOnline,
            LocalDate startDate,
            LocalDate endDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        validateTitle(title);
        validateDescription(description);
        validateIsOnline(isOnline);
        validateClassDates(startDate, endDate);
        validateClassTimes(startTime, endTime);
    }

    /**
     * 클래스 수정 시 전체 검증
     * - 강사, 온라인 여부는 수정 불가 가정
     * - 시작일 과거 허용
     *
     * @param title 클래스 제목
     * @param description 클래스 설명
     * @param startDate 강의 시작일
     * @param endDate 강의 종료일
     * @param startTime 강의 시작 시간
     * @param endTime 강의 종료 시간
     * @throws CustomException 검증 실패 시
     */
    public static void validateUpdate(
            String title,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
        validateTitle(title);
        validateDescription(description);
        validateClassDatesForUpdate(startDate, endDate);
        validateClassTimes(startTime, endTime);
    }
}