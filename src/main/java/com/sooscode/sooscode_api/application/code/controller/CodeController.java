package com.sooscode.sooscode_api.application.code.controller;

import com.sooscode.sooscode_api.application.websocket.dto.CodeShareDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CodeController {
    // 코드 공유 (단순 브로드캐스트)
    @MessageMapping("/code/{classId}")
    @SendTo("/topic/class/{classId}/code")
    public CodeShareDto shareCode(
            @DestinationVariable Long classId,
            CodeShareDto codeShare
    ) {
        log.info("Code shared - classId: {}, user: {}, language: {}, length: {}",
                classId, codeShare.getUserName(), codeShare.getLanguage(),
                codeShare.getCode() != null ? codeShare.getCode().length() : 0);

        codeShare.setClassId(classId);

        return codeShare;
    }
}
