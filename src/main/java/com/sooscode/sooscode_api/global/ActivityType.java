package com.sooscode.sooscode_api.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivityType {

    LOGIN("로그인"),
    LOGOUT("로그아웃"),
    CLASS_ENTER("클래스 입장"),
    CLASS_EXIT("클래스 퇴장");

    private final String displayName;
}