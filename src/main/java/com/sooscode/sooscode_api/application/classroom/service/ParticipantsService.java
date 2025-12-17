package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.ParticipantInfo;
import com.sooscode.sooscode_api.application.classroom.dto.ParticipantMessage;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.websocket.WebSocketSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipantsService {

    private final WebSocketSessionRegistry sessionRegistry;
    private final UserRepository userRepository;

    /**
     * 특정 클래스의 현재 참가자 목록 조회
     */
    public ParticipantMessage getParticipants(Long classId) {

        // 1. classId에 속한 userId 목록 조회
        var userIds = sessionRegistry.getClassMembers(classId.toString());

        log.info("클래스 {} 참가자 수: {}", classId, userIds.size());

        // 2. userId 목록으로 User 엔티티 한 번에 조회
        Set<Long> userIdSet = userIds.stream()
                .map(obj -> ((Number) obj).longValue())
                .collect(Collectors.toSet());

        Map<Long, User> userMap = userRepository.findAllById(userIdSet).stream()
                .collect(Collectors.toMap(User::getUserId, user -> user));

        // 3. ParticipantInfo 생성
        List<ParticipantInfo> participant =
                userIds.stream()
                        .map(obj -> {
                            Long userId = ((Number) obj).longValue();

                            String sessionId =
                                    sessionRegistry.getExistingSessionId(userId);

                            boolean isInstructor =
                                    sessionId != null && sessionRegistry.isInstructor(sessionId);

                            // User 엔티티에서 실제 이름 가져오기
                            User user = userMap.get(userId);
                            String username = user != null ? user.getName() : "User #" + userId;
                            String userEmail = user != null ? user.getEmail() : null;

                            return ParticipantInfo.builder()
                                    .userId(userId)
                                    .username(username) // 실제 이름
                                    .userEmail(userEmail) // 실제 이메일
                                    .role(isInstructor ? "INSTRUCTOR" : "STUDENT")
                                    .isOnline(sessionId != null)
                                    .build();
                        })
                        .toList();

        return ParticipantMessage.builder()
                .classId(classId)
                .participant(participant)
                .build();
    }
}