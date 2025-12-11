package com.sooscode.sooscode_api.global.websocket;

import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketSessionRegistry implements ChannelInterceptor {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis 키 prefix/suffix 상수
     * - 세션 키: ws:session:{sessionId} → Hash (userId, classId)
     * - 클래스 멤버 키: ws:class:{classId}:members → Set (userId 목록)
     */
    private static final String SESSION_KEY_PREFIX = "ws:session:";
    private static final String CLASS_MEMBERS_KEY_PREFIX = "ws:class:";
    private static final String CLASS_MEMBERS_KEY_SUFFIX = ":members";
    private static final String USER_SESSION_KEY_PREFIX = "ws:user:";
    private static final String USER_SESSION_KEY_SUFFIX = ":session";
    private static final long SESSION_TTL_HOURS = 24;


    /**
     * STOMP 메시지를 가로채서 CONNECT / SUBSCRIBE / DISCONNECT 이벤트를 처리하는 메서드
     * - CONNECT: WebSocket 연결 시 세션 생성 (userId 저장)
     * - SUBSCRIBE: 채팅 채널 구독 시 클래스 입장 처리 (classId 저장, 멤버 목록 추가)
     * - DISCONNECT: 연결 종료 시 세션 삭제 및 클래스 멤버 목록에서 제거
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        // STOMP 메시지의 헤더를 파싱하기 위한 접근자
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        // 현재 들어온 STOMP 명령 (CONNECT, SUBSCRIBE, SEND, DISCONNECT 등)
        StompCommand command = accessor.getCommand();


        /*
         * ========== CONNECT ==========
         * 클라이언트가 WebSocket 핸드셰이크를 완료하면 STOMP CONNECT 명령이 들어옴
         * 이 시점에는 Spring Security가 JWT 인증을 끝낸 상태이므로
         * accessor.getUser()에서 인증 정보를 꺼낼 수 있다.
         *
         * Redis 저장 구조:
         * 1. 세션 정보
         *    - Key: ws:session:{sessionId}
         *    - Type: Hash
         *    - Field: userId → 유저 ID
         *    - TTL: 24시간
         *
         * 2. 유저별 현재 세션 (중복 접속 방지용)
         *    - Key: ws:user:{userId}:session
         *    - Type: String
         *    - Value: sessionId
         *
         * 중복 접속 처리:
         * - 같은 유저가 다른 기기/탭에서 접속하면 기존 세션 Redis에서 정리
         * - 기존 세션은 클라이언트에서 연결 끊김 감지하여 알림 처리
         */
        if (StompCommand.CONNECT.equals(command)) {

            // 인증 정보가 UsernamePasswordAuthenticationToken 인지 확인
            if (accessor.getUser() instanceof UsernamePasswordAuthenticationToken auth
                    && auth.getPrincipal() instanceof CustomUserDetails userDetails) {

                // JWT에서 추출된 유저 정보
                Long userId = userDetails.getUser().getUserId();

                // WebSocket 고유 세션 ID (같은 유저라도 여러 브라우저 탭이면 다름)
                String sessionId = accessor.getSessionId();

                // ========== 중복 접속 체크 ==========
                // 해당 유저의 기존 세션이 있는지 확인
                String userSessionKey = USER_SESSION_KEY_PREFIX + userId + USER_SESSION_KEY_SUFFIX;
                String oldSessionId = (String) redisTemplate.opsForValue().get(userSessionKey);

                // 기존 세션이 있고, 현재 세션과 다르면 → 기존 세션 정리
                if (oldSessionId != null && !oldSessionId.equals(sessionId)) {

                    // 기존 세션의 클래스 입장 정보 정리
                    String oldSessionKey = SESSION_KEY_PREFIX + oldSessionId;
                    Object oldClassId = redisTemplate.opsForHash().get(oldSessionKey, "classId");

                    // 클래스에 입장한 상태였다면 멤버 목록에서 제거
                    if (oldClassId != null) {
                        String classKey = CLASS_MEMBERS_KEY_PREFIX + oldClassId + CLASS_MEMBERS_KEY_SUFFIX;
                        redisTemplate.opsForSet().remove(classKey, userId);
                        log.info("FORCE CLASS LEAVE — classId={}, userId={}", oldClassId, userId);
                    }

                    // 기존 세션 삭제
                    redisTemplate.delete(oldSessionKey);

                    log.info("FORCE DISCONNECT — oldSessionId={}, newSessionId={}, userId={}",
                            oldSessionId, sessionId, userId);
                }

                // ========== 새 세션 등록 ==========
                // Redis Hash에 userId 저장
                String sessionKey = SESSION_KEY_PREFIX + sessionId;
                redisTemplate.opsForHash().put(sessionKey, "userId", userId);
                redisTemplate.expire(sessionKey, SESSION_TTL_HOURS, TimeUnit.HOURS);

                // 유저 → 세션 매핑 갱신 (다음 중복 체크용)
                redisTemplate.opsForValue().set(userSessionKey, sessionId);

                log.info("WS CONNECT — sessionId={}, userId={}", sessionId, userId);
            }
        }

        /*
         * ========== SUBSCRIBE ==========
         * 클라이언트가 특정 채널을 구독할 때 호출됨
         * 채팅 채널(/topic/class/{classId}/chat) 구독 시에만 클래스 입장으로 처리
         *
         * Redis 저장 구조:
         * 1. 세션에 classId 추가
         *    - Key: ws:session:{sessionId}
         *    - Field: classId → 클래스 ID
         *
         * 2. 클래스 멤버 목록에 userId 추가
         *    - Key: ws:class:{classId}:members
         *    - Type: Set
         *    - Value: userId 목록
         */
        else if (StompCommand.SUBSCRIBE.equals(command)) {

            // 구독 대상 경로 (예: /topic/class/123/chat)
            String destination = accessor.getDestination();
            String sessionId = accessor.getSessionId();

            // 채팅 채널 구독인 경우에만 입장 처리
            // /topic/class/{classId}/chat 패턴 체크
            if (destination != null
                    && destination.startsWith("/topic/class/")
                    && destination.endsWith("/chat")) {

                Long userId = getUserId(sessionId);
                String classId = extractClassId(destination);

                if (userId != null && classId != null) {

                    // 세션 Hash에 classId 추가
                    String sessionKey = SESSION_KEY_PREFIX + sessionId;
                    redisTemplate.opsForHash().put(sessionKey, "classId", classId);

                    // 클래스 멤버 Set에 userId 추가
                    String classKey = CLASS_MEMBERS_KEY_PREFIX + classId + CLASS_MEMBERS_KEY_SUFFIX;
                    redisTemplate.opsForSet().add(classKey, userId);

                    log.info("CLASS JOIN — classId={}, userId={}, sessionId={}", classId, userId, sessionId);
                }
            }
        }


        /*
         * ========== DISCONNECT ==========
         * 사용자가 WebSocket을 종료(브라우저 탭 닫기 등)하면 해당 세션을 정리
         *
         * 처리 순서:
         * 1. 세션에서 userId, classId 조회
         * 2. 클래스 멤버 목록에서 userId 제거 (classId가 있는 경우)
         * 3. 세션 키 삭제
         */
        else if (StompCommand.DISCONNECT.equals(command)) {

            String sessionId = accessor.getSessionId();
            String sessionKey = SESSION_KEY_PREFIX + sessionId;

            // 세션에서 userId, classId 조회
            Object userId = redisTemplate.opsForHash().get(sessionKey, "userId");
            Object classId = redisTemplate.opsForHash().get(sessionKey, "classId");

            // 클래스에 입장한 상태였다면 멤버 목록에서 제거
            if (classId != null && userId != null) {
                String classKey = CLASS_MEMBERS_KEY_PREFIX + classId + CLASS_MEMBERS_KEY_SUFFIX;
                redisTemplate.opsForSet().remove(classKey, userId);
                log.info("CLASS LEAVE — classId={}, userId={}", classId, userId);
            }

            // 세션 삭제
            redisTemplate.delete(sessionKey);
            log.info("WS DISCONNECT — sessionId={}, userId={}", sessionId, userId);
        }


        return message;
    }


    /**
     * 세션 ID로 유저 ID를 조회
     * - 특정 메시지가 어느 사용자의 것인지 확인할 때 사용
     * - 예: 채팅방에서 "이 메시지를 보낸 유저는 누구인가?"
     *
     * @param sessionId WebSocket 세션 ID
     * @return 유저 ID (없으면 null)
     */
    public Long getUserId(String sessionId) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        Object value = redisTemplate.opsForHash().get(sessionKey, "userId");
        return value != null ? ((Number) value).longValue() : null;
    }


    /**
     * 세션 ID로 클래스 ID를 조회
     * - 특정 세션이 어느 클래스에 입장해 있는지 확인할 때 사용
     *
     * @param sessionId WebSocket 세션 ID
     * @return 클래스 ID (입장 전이면 null)
     */
    public String getClassId(String sessionId) {
        String sessionKey = SESSION_KEY_PREFIX + sessionId;
        Object value = redisTemplate.opsForHash().get(sessionKey, "classId");
        return value != null ? value.toString() : null;
    }


    /**
     * 클래스에 현재 접속 중인 멤버 목록 조회
     * - 강사가 "지금 이 클래스에 누가 있는지" 확인할 때 사용
     * - 실시간 접속자 수 표시에 활용
     *
     * @param classId 클래스 ID
     * @return 접속 중인 userId Set (없으면 빈 Set)
     */
    public Set<Object> getClassMembers(String classId) {
        String classKey = CLASS_MEMBERS_KEY_PREFIX + classId + CLASS_MEMBERS_KEY_SUFFIX;
        return redisTemplate.opsForSet().members(classKey);
    }


    /**
     * destination 경로에서 classId 추출
     * - /topic/class/{classId}/chat 형태에서 classId 부분만 파싱
     *
     * @param destination 구독 경로 (예: /topic/class/123/chat)
     * @return 클래스 ID (파싱 실패 시 null)
     */
    private String extractClassId(String destination) {
        // /topic/class/{classId}/chat → ["", "topic", "class", "123", "chat"]
        String[] parts = destination.split("/");
        if (parts.length >= 4) {
            return parts[3];
        }
        return null;
    }
}