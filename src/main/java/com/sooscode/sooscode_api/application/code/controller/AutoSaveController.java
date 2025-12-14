package com.sooscode.sooscode_api.application.code.controller;

import com.sooscode.sooscode_api.application.code.dto.AutoSaveDto;
import com.sooscode.sooscode_api.application.code.service.AutoSaveService;
import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AutoSaveController {

    private final AutoSaveService autoSaveService;

    /**
     * 자동 저장된 코드 불러오기
     * GET /api/code/auto-save?classId={classId}
     */
    @GetMapping("/api/code/auto-save")
    public ResponseEntity<AutoSaveDto> getAutoSavedCode(
            @RequestParam Long classId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        log.info("AUTO-SAVE LOAD userDetails={}", user);

        if (user == null) {
            return ResponseEntity.noContent().build();
        }

        Long userId = user.getUser().getUserId();
        log.info("AUTO-SAVE LOAD userId={}", userId);

        AutoSaveDto autoSaved = autoSaveService.getAutoSaved(classId, userId);

        if (autoSaved == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(autoSaved);
    }
}