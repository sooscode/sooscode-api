package com.sooscode.sooscode_api.global.websocket;

import com.sooscode.sooscode_api.global.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketSessionRegistry implements ChannelInterceptor {

    /**
     * WebSocket 세션ID → 유저ID 매핑 저장소
     * - 여러 스레드에서 동시에 접근하므로 ConcurrentHashMap 사용
     * - 웹소켓 연결 후 사용자가 누군지 확인하기 위한 핵심 자료구조
     */
    private final Map<String, Long> sessionUserMap = new ConcurrentHashMap<>();


    /**
     * STOMP 메시지를 가로채서 CONNECT / DISCONNECT 이벤트를 처리하는 메서드
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
         * CONNECT
         * 클라이언트가 웹소켓 핸드셰이크를 완료하면 STOMP CONNECT 명령이 들어옴
         * 이 시점에는 Spring Security가 JWT 인증을 끝낸 상태이므로
         * accessor.getUser()에서 인증 정보를 꺼낼 수 있다.
         */
        if (StompCommand.CONNECT.equals(command)) {

            // 인증 정보가 UsernamePasswordAuthenticationToken 인지 확인
            // 항상 내가 기대하는 타입이 아닐 수도 있기 때문에 확인해야 함
            if (accessor.getUser() instanceof UsernamePasswordAuthenticationToken auth
                    && auth.getPrincipal() instanceof CustomUserDetails userDetails) {

                // JWT 에서 추출된 유저 정보 (SecurityContext에 저장되어 있음)
                Long userId = userDetails.getUser().getUserId();

                // 웹소켓 고유 세션 ID (같은 유저라도 여러 브라우저 탭이면 다름)
                String sessionId = accessor.getSessionId();

                // 세션ID → userId 저장
                sessionUserMap.put(sessionId, userId);

                log.info("WS CONNECT — sessionId={}, userId={}", sessionId, userId);
            }
        }


        /*
         * DISCONNECT 처리
         * 사용자가 웹소켓을 종료(브라우저 탭 닫기 등)하면
         * 해당 sessionId 를 매핑에서 제거한다.
         */
        else if (StompCommand.DISCONNECT.equals(command)) {

            String sessionId = accessor.getSessionId();

            // 저장된 userId 제거
            Long removed = sessionUserMap.remove(sessionId);

            log.info("WS DISCONNECT — sessionId={}, userId={}", sessionId, removed);
        }


        return message;
    }


    /**
     * 외부에서 세션ID로 유저ID를 조회할 때 사용
     * - 특정 메시지가 어느 사용자의 것인지 확인할 때 사용됨
     * - 예: 채팅방에서 "이 메시지를 보낸 유저는 누구인가?"
     */
    public Long getUserId(String sessionId) {
        return sessionUserMap.get(sessionId);
    }
}