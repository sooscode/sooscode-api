package com.sooscode.sooscode_api.infra.worker;

import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.CompileStatus;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeBlacklistFilter {

    // 금지 패턴 목록
    private static final List<Pattern> BLACKLIST_PATTERNS = List.of(

            // ===== 시스템 제어 =====
            Pattern.compile("System\\s*\\.\\s*exit"),

            // ===== 외부 프로세스 =====
            Pattern.compile("Runtime\\s*\\.\\s*getRuntime"),
            Pattern.compile("ProcessBuilder"),
            Pattern.compile("exec\\s*\\("),

            // ===== 파일/디렉토리 IO =====
            Pattern.compile("java\\s*\\.\\s*io\\s*\\.\\s*File"),
            Pattern.compile("FileInputStream"),
            Pattern.compile("FileOutputStream"),
            Pattern.compile("Files\\s*\\.\\s*read"),
            Pattern.compile("Files\\s*\\.\\s*write"),
            Pattern.compile("Paths\\s*\\.\\s*get"),
            Pattern.compile("Path\\s*\\.\\s*of"),

            // ===== 네트워크 IO =====
            Pattern.compile("Socket"),
            Pattern.compile("ServerSocket"),
            Pattern.compile("URLConnection"),
            Pattern.compile("HttpURLConnection"),
            Pattern.compile("openStream"),
            Pattern.compile("InetAddress"),

            // ===== Reflection / ClassLoader =====
            Pattern.compile("java\\s*\\.\\s*lang\\s*\\.\\s*reflect"),
            Pattern.compile("Class\\s*\\.\\s*forName"),
            Pattern.compile("ClassLoader"),
            Pattern.compile("getDeclared"),
            Pattern.compile("invoke"),
            Pattern.compile("setAccessible"),

            // ===== Thread / Executor =====
            Pattern.compile("new\\s+Thread"),
            Pattern.compile("Thread\\s*\\.\\s*sleep"),
            Pattern.compile("Executor"),
            Pattern.compile("ForkJoinPool"),
            Pattern.compile("TimerTask"),

            // ===== 무한 루프 =====
            Pattern.compile("while\\s*\\(\\s*true\\s*\\)"),
            Pattern.compile("for\\s*\\(\\s*;\\s*;\\s*\\)"),


            // ===== Unsafe / Native =====
            Pattern.compile("Unsafe"),
            Pattern.compile("System\\s*\\.\\s*load"),
            Pattern.compile("sun\\s*\\.\\s*misc"),

            // ===== 난독화 =====
            Pattern.compile("\\\\u[0-9a-fA-F]{4}") // Unicode escape
    );

    /**
     * 코드 내부에 금지된 패턴이 포함되어 있으면 예외 발생
     */
    public static void validate(String code) {
        if (code == null || code.isBlank()) return;

        String normalized = code;

        for (Pattern pattern : BLACKLIST_PATTERNS) {
            Matcher matcher = pattern.matcher(normalized);
            if (matcher.find()) {
                throw new CustomException(CompileStatus.FORBIDDEN_SYNTAX);
            }
        }
    }
}
