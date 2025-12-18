package com.sooscode.sooscode_api.global.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatStatus implements StatusCode {

    OK(HttpStatus.OK, "CHAT_000", "채팅이 성공적으로 입력되었습니다"),
    READ_OK(HttpStatus.OK, "CHAT_001", "채팅이 성공적으로 조회되었습니다"),
    ENTER_OK(HttpStatus.OK, "CHAT_001", "채팅방에 성공적으로 입장되었습니다"),
    EXIT_OK(HttpStatus.OK, "CHAT_001", "채팅방에 성공적으로 퇴장되었습니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_004", "채팅을 찾을 수 없습니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "CHAT_005", "채팅 접근 권한이 없습니다"),
    DELETE_OK(HttpStatus.OK, "CHAT_006", "채팅방에 성공적으로 삭제되었습니다"),
    NOT_EMPTY(HttpStatus.FORBIDDEN, "CHAT_007", "채팅을 입력하세요"),
    CONTENT_TOO_LONG(HttpStatus.FORBIDDEN, "CHAT_008", "채팅을 입력하세요");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
