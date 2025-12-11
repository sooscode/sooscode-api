package com.sooscode.sooscode_api.application.chat.service;

public interface ChatPresenceService {
    // 유저가 이미 들어온 상태인지 아닌지
    boolean isAlreadyIn(Long userId, Long classId);
    // 유저가 이미 나간 상태인지 아닌지
    boolean isAleadyOut(Long userId, Long classId);
    // 유저를 입장 상태로 표시
    void markEnter(Long userId, Long classId);
    // 유저를 퇴장 상태로 표시
    void markExit(Long userId, Long classId);

    //presence.put("2:1", true);    -> 유저 2는 class 1 안에 있음
    //presence.put("2:1", false);   -> 유저 2는 class 1에서 나감
}
