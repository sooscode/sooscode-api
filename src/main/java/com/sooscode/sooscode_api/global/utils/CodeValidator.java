package com.sooscode.sooscode_api.global.utils;

import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.status.CodeValidStatus;
import com.sooscode.sooscode_api.global.status.CompileStatus;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Java 코드 유효성 검증 유틸리티
 */
public final class CodeValidator {

    private static final int MAX_CODE_LENGTH = 1000;
    private static final int MAX_OUTPUT_LINES = 80;

    private static final Pattern MAIN_CLASS_PATTERN =
            Pattern.compile("public\\s+class\\s+Main\\b");

    private static final List<String> FORBIDDEN_KEYWORDS = List.of(
            "Runtime.getRuntime",
            "System.exit",
            "ProcessBuilder",
            "Files.readAllBytes",
            "Class.forName",
            "Thread.sleep",
            "while(true)",
            "while (true)"
    );

    // 생성자 막기
    private CodeValidator() {}

    /**
     * 전체 코드 유효성 검사
     */
    public static void validateAll(String code) {

        validateRequired(code);
        validateEncoding(code);
        validateLength(code);
        validateLineCount(code);
        validateMainClass(code);
        validateForbiddenSyntax(code);
    }

    /** 코드 null/empty 검사 */
    public static void validateRequired(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new CustomException(CodeValidStatus.CODE_REQUIRED);
        }
    }

    /** UTF-8 인코딩 가능 여부 검사 */
    public static void validateEncoding(String code) {
        if (!StandardCharsets.UTF_8.newEncoder().canEncode(code)) {
            throw new CustomException(CodeValidStatus.CODE_INVALID_ENCODING);
        }
    }

    /** 코드 길이 검사 */
    public static void validateLength(String code) {
        if (code.length() > MAX_CODE_LENGTH) {
            throw new CustomException(CodeValidStatus.CODE_TOO_LONG);
        }
    }

    /** 코드 라인 수 검사 (옵션) */
    public static void validateLineCount(String code) {
        int lines = code.split("\r\n|\r|\n").length;

        if (lines > MAX_OUTPUT_LINES) {
            throw new CustomException(CodeValidStatus.CODE_TOO_MANY_LINES);
        }
    }

    /** Main 클래스 존재 검사 */
    public static void validateMainClass(String code) {
        if (!MAIN_CLASS_PATTERN.matcher(code).find()) {
            throw new CustomException(CodeValidStatus.MAIN_CLASS_REQUIRED);
        }
    }

    /** 금지된 문법/키워드 검사 */
    public static void validateForbiddenSyntax(String code) {

        for (String keyword : FORBIDDEN_KEYWORDS) {
            if (code.contains(keyword)) {
                throw new CustomException(CompileStatus.FORBIDDEN_SYNTAX);
            }
        }
    }
}
