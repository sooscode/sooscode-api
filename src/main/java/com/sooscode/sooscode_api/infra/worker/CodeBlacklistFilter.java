package com.sooscode.sooscode_api.infra.worker;

import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.CompileStatus;

import java.util.List;

public class CodeBlacklistFilter {

    // 금지 패턴 목록
    private static final List<String> BLACKLIST_PATTERNS = List.of(
            "System.exit",
            "Runtime.getRuntime",
            "ProcessBuilder",
            "java.io.File",
            "Files.read",
            "Files.write",
            "Class.forName",
            "ClassLoader",
            "Thread.sleep",
            "exec(",
            "new Socket",
            "URLConnection",
            "URL ",
            "System.setProperty",
            "java.lang.reflect"
    );

    /**
     * 코드 내부에 금지된 패턴이 포함되어 있으면 예외 발생
     */
    public static void validate(String code) {
        if (code == null) return;

        for (String banned : BLACKLIST_PATTERNS) {
            if (code.contains(banned)) {
                throw new CustomException(CompileStatus.NOT_FOUND);
            }
        }
    }
}
