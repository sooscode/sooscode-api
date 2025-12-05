// 2. WebSocketConfig.java
package com.sooscode.sooscode_api.global.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP 기반 WebSocket 설정 파일
 * - /ws 엔드포인트로 WebSocket 연결
 * - /app prefix → Controller @MessageMapping 으로 라우팅
 * - /topic, /queue → 브로커가 메시지 브로드캐스트 처리
 * - WebSocketSessionRegistry 를 inbound channel Interceptor 로 등록하여
 *   CONNECT / DISCONNECT 에서 인증 정보 연동 수행
 */
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * STOMP 메시지가 서버로 들어올 때 감시하기 위한 인터셉터
     * - CONNECT 요청 시 sessionId ↔ userId 매핑 저장
     * - DISCONNECT 시 매핑 제거
     */
    private final WebSocketSessionRegistry sessionRegistry;


    /**
     * 클라이언트 → 서버 방향(STOMP INBOUND)의 메시지를 인터셉트하는 단계
     * - STOMP CONNECT / DISCONNECT 메시지를 가로채어 인증 사용자 세션 등록하기 위함
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(sessionRegistry);   // WebSocket 인증/세션 관리 Interceptor 연결
    }


    /**
     * WebSocket(STOMP) 연결 엔드포인트 등록
     * - 클라이언트는 ws://localhost:8080/ws 로 접속
     * - SockJS fallback 지원
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")            // WebSocket 연결 URL
                .setAllowedOriginPatterns("*") // 모든 Origin 허용
                .withSockJS();                 // SockJS 사용 (브라우저 지원 fallback)
    }


    /**
     * 메시지 브로커 설정
     * - /topic, /queue → 브로커가 메시지 전달 처리 (구독 기반)
     * - /app → @MessageMapping 으로 매핑되는 서버 전용 라우팅 prefix
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");  // 메시지 구독 경로
        registry.setApplicationDestinationPrefixes("/app"); // Controller 메시지 핸들링 prefix
    }
}