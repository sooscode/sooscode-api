package com.sooscode.sooscode_api.application.chatting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker  // STOMP 메시지 브로커 활성화
public class WebsocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 구독(prefix): 클라이언트가 구독할 주소
        registry.enableSimpleBroker("/topic"); // 예: /topic/chat

        // 발행(prefix): 클라이언트가 send 할 때 붙이는 prefix
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")  // 개발용 - CORS 허용
                .withSockJS();                  // SockJS 사용
    }
}
