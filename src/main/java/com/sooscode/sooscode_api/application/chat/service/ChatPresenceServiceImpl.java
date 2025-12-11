package com.sooscode.sooscode_api.application.chat.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatPresenceServiceImpl implements ChatPresenceService {
    // key: "userId:classId", value: true/false
    private final ConcurrentHashMap<String, Boolean> presence = new ConcurrentHashMap<>();

    private String key(Long userId, Long classId) {
        return userId + ":" + classId;
    }

    @Override
    public boolean isAlreadyIn(Long userId, Long classId) {
        return Boolean.TRUE.equals(presence.get(key(userId, classId)));
    }
    @Override
    public boolean isAleadyOut(Long userId, Long classId){
        return !Boolean.TRUE.equals(presence.get(key(userId, classId)));
    }

    @Override
    public void markEnter(Long userId, Long classId) {
        presence.put(key(userId, classId), true);
    }

    @Override
    public void markExit(Long userId, Long classId) {
        presence.put(key(userId, classId), false);
    }
}
