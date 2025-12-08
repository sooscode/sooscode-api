package com.sooscode.sooscode_api.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class AsyncSecurityConfig {

    /**
     *   HTTP 요청 스레드랑 future 스레드가 다른 스레드라서 토큰 인증정보를 가지고가지 못함
     * - Spring Security Context를 비동기 스레드에도 전달하기 위한 전용 Executor.
     * - DelegatingSecurityContextExecutor가 현재 요청의 인증 정보를 복사
     *   비동기 스레드에서 동일하게 사용할 수 있도록 보장
     */
    @Bean
    public Executor securityAsyncExecutor() {
        return new DelegatingSecurityContextExecutor(
                Executors.newFixedThreadPool(10)
        );
    }
}
