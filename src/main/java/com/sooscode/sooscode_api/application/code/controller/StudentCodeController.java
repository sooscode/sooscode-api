package com.sooscode.sooscode_api.application.code.controller;

import com.sooscode.sooscode_api.application.code.dto.AutoSaveDto;
import com.sooscode.sooscode_api.application.code.dto.CodeShareDto;
import com.sooscode.sooscode_api.application.code.dto.StudentCodeResponse;
import com.sooscode.sooscode_api.application.code.service.AutoSaveService;
import com.sooscode.sooscode_api.global.websocket.WebSocketSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
class CodeRestController {

    private final AutoSaveService autoSaveService;

    /**
     * 특정 학생의 최신 코드 조회
     */
    @GetMapping("/api/code/student/{classId}/{studentId}")
    public ResponseEntity<StudentCodeResponse> getStudentCode(
            @PathVariable Long classId,
            @PathVariable Long studentId
    ) {
        try {
            AutoSaveDto autoSaveDto = autoSaveService.getAutoSaved(classId, studentId);

            if (autoSaveDto == null) {
                return ResponseEntity.notFound().build();
            }

            StudentCodeResponse response = StudentCodeResponse.builder()
                    .userId(autoSaveDto.getUserId())
                    .username("User #" + studentId)
                    .code(autoSaveDto.getCode())
                    .language(autoSaveDto.getLanguage())
                    .classId(classId)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("학생 코드 조회 실패: classId={}, studentId={}", classId, studentId, e);
            return ResponseEntity.internalServerError().build();
        }
    }



}
