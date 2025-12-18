package com.sooscode.sooscode_api.global.utils;

import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.status.ValidStatus;

import java.util.regex.Pattern;

/**
 * 사용자 입력 유효성 검증 유틸리티 클래스
 * 클라이언트 측 검증과 동일한 규칙 적용
 */
public class UserValidator {

    // 정규식 패턴
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/~`|\\\\]{6,16}$");

    /**
     * 이름 유효성 검증 (2~16자)
     * @throws CustomException
     */
    public static void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new CustomException(ValidStatus.NAME_REQUIRED);
        }

        String trimmed = username.trim();
        if (trimmed.length() < 2) {
            throw new CustomException(ValidStatus.NAME_TOO_SHORT);
        }
        if (trimmed.length() > 16) {
            throw new CustomException(ValidStatus.NAME_TOO_LONG);
        }
    }

    /**
     * 이메일 유효성 검증 (형식 + 최대 50자)
     * @throws CustomException
     */
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new CustomException(ValidStatus.EMAIL_REQUIRED);
        }

        String trimmed = email.trim();
        if (trimmed.length() < 5) {
            throw new CustomException(ValidStatus.EMAIL_TOO_SHORT);
        }

        if (trimmed.length() > 50) {
            throw new CustomException(ValidStatus.EMAIL_TOO_LONG);
        }

        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new CustomException(ValidStatus.EMAIL_INVALID_FORMAT);
        }
    }

    /**
     * 비밀번호 유효성 검증 (8~16자, 영문+숫자 필수)
     * @throws CustomException
     */
    public static void validatePassword(String password) {

        if (password == null || password.isEmpty()) {
            throw new CustomException(ValidStatus.PASSWORD_REQUIRED);
        }

        if (password.length() < 8) {
            throw new CustomException(ValidStatus.PASSWORD_TOO_SHORT);
        }
        if (password.length() > 16) {
            throw new CustomException(ValidStatus.PASSWORD_TOO_LONG);
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new CustomException(ValidStatus.PASSWORD_INVALID_FORMAT);
        }
    }

    /**
     * 비밀번호 길이만 검증 (8~16자)
     * @throws CustomException
     */
    public static void validatePasswordLength(String password) {
        if (password == null || password.isEmpty()) {
            throw new CustomException(ValidStatus.PASSWORD_REQUIRED);
        }

        if (password.length() < 8) {
            throw new CustomException(ValidStatus.PASSWORD_TOO_SHORT);
        }

        if (password.length() > 16) {
            throw new CustomException(ValidStatus.PASSWORD_TOO_LONG);
        }
    }


    /**
     * 비밀번호 확인 검증
     * @throws CustomException
     */
    public static void validatePasswordConfirm(String password, String passwordConfirm) {
        if (passwordConfirm == null || passwordConfirm.isEmpty()) {
            throw new CustomException(ValidStatus.PASSWORD_REQUIRED);
        }

        if (!password.equals(passwordConfirm)) {
            throw new CustomException(ValidStatus.PASSWORD_MISMATCH);
        }
    }

    public static UserRole validateRole(String role) {

        if (role == null || role.isBlank()) {
            throw new CustomException(ValidStatus.ROLE_REQUIRED);
        }
        // 문자열 → Enum 변환
        UserRole parsedRole;
        try {
            parsedRole = UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ValidStatus.INVALID_USER_ROLE);
        }

        // ADMIN 거부
        if (parsedRole == UserRole.ADMIN) {
            throw new CustomException(ValidStatus.ADMIN_ROLE_NOT_ALLOWED);
        }

        // 허용 권한 체크
        if (parsedRole != UserRole.INSTRUCTOR && parsedRole != UserRole.STUDENT) {
            throw new CustomException(ValidStatus.INVALID_USER_ROLE);
        }

        return parsedRole;
    }

    /**
     * 문자열이 null 또는 빈 값인지 확인 후 null로 변환
     * 필터링 파라미터 전처리용
     */
    public static String convertEmptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    /**
     * 전체 회원가입 데이터 검증
     * @throws CustomException 유효성 검증에 걸리면 커스텀 예외 발생
     */
    public static void validateSignupData(
            String username,
            String email,
            String password,
            String passwordConfirm) {

        validateUsername(username);
        validateEmail(email);
        validatePassword(password);
        validatePasswordConfirm(password, passwordConfirm);
    }
}