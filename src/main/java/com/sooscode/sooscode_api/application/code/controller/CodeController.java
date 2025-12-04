package com.sooscode.sooscode_api.application.code.controller;

import com.sooscode.sooscode_api.application.code.dto.CodeShareDto;
import com.sooscode.sooscode_api.global.user.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CodeController {

    @MessageMapping("/code/{classId}")
    @SendTo("/topic/class/{classId}/code")
    public CodeShareDto shareCode(
            @DestinationVariable Long classId,
            @Payload CodeShareDto codeShare,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // null 체크
        if (userDetails == null || userDetails.getUser() == null) {
            log.error("❌ UserDetails or User is null!");
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        // 서버에서 유저 정보 설정
        codeShare.setClassId(classId);
        codeShare.setUserId(userDetails.getUser().getUserId());
        codeShare.setUserName(userDetails.getUser().getName());

        log.info("✅ Code shared - classId: {}, userId: {}, userName: {}, codeLength: {}",
                classId,
                userDetails.getUser().getUserId(),
                userDetails.getUser().getName(),
                codeShare.getCode() != null ? codeShare.getCode().length() : 0);

        return codeShare;
    }
}