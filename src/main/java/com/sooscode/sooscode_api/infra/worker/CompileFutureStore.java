package com.sooscode.sooscode_api.infra.worker;

import com.sooscode.sooscode_api.application.compile.dto.CompileResultResponse;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.errorcode.CompileErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CompileFutureStore {

    // Job ID와 CompletableFuture<CompileResultResponse>를 스레드 안전하게 저장
    private final Map<String, CompletableFuture<CompileResultResponse>> futureMap = new ConcurrentHashMap<>();

    // 최대 대기 시간 (15초)
    private static final long TIMEOUT_SECONDS = 1500;

    /**
     * 새로운 Job에 대한 비동기 대기 객체를 생성하고 저장합니다.
     */
    public CompletableFuture<CompileResultResponse> createFuture(String jobId) {

        // LOG 추가
        log.info("[FutureStore] createFuture() called - jobId={}", jobId);

        CompletableFuture<CompileResultResponse> future = new CompletableFuture<>();
        futureMap.put(jobId, future);

        // LOG 추가
        log.info("[FutureStore] keys after create = {}", futureMap.keySet());

        // 타임아웃 처리 로직 추가 (15초 초과 시 TIMEOUT 에러 발생)
        future.orTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .exceptionally(ex -> {

                    // LOG 추가
                    log.warn("[FutureStore] TIMEOUT or exception occurred - jobId={}, ex={}", jobId, ex.toString());

                    if (ex.getCause() instanceof java.util.concurrent.TimeoutException) {
                        futureMap.remove(jobId);

                        // LOG 추가
                        log.warn("[FutureStore] Future removed due to TIMEOUT - jobId={}", jobId);

                        throw new CustomException(CompileErrorCode.WORKER_TIMEOUT);
                    }

                    futureMap.remove(jobId);

                    // LOG 추가
                    log.warn("[FutureStore] Future removed due to OTHER EXCEPTION - jobId={}", jobId);

                    throw new CustomException(CompileErrorCode.NOT_FOUND);
                });

        return future;
    }

    /**
     * 워커 서버로부터 콜백을 받았을 때 비동기 대기 객체를 완료합니다.
     */
    public void completeFuture(String jobId, CompileResultResponse result) {

        // LOG 추가
        log.info("[FutureStore] completeFuture() called - jobId={}", jobId);
        log.info("[FutureStore] keys before remove = {}", futureMap.keySet());

        // 맵에서 퓨처를 꺼내면서 제거 (한 번만 완료되어야 함)
        CompletableFuture<CompileResultResponse> future = futureMap.remove(jobId);

        if (future != null) {

            // LOG 추가
            log.info("[FutureStore] future FOUND - completing jobId={}", jobId);

            // Future를 완료시키고, 대기 중이던 API 요청 연결에 결과를 전달합니다.
            future.complete(result);

        } else {

            // LOG 추가
            log.warn("[FutureStore] future NOT FOUND - jobId={} (PENDING 발생 원인)", jobId);
        }
    }
}
