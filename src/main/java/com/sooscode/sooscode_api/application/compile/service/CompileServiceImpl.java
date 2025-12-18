package com.sooscode.sooscode_api.application.compile.service;

import com.sooscode.sooscode_api.application.compile.dto.CompileResultResponse;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.status.CompileStatus;
import com.sooscode.sooscode_api.global.guard.RequestCooldown;
import com.sooscode.sooscode_api.global.utils.CodeValidator;
import com.sooscode.sooscode_api.infra.worker.CodeBlacklistFilter;
import com.sooscode.sooscode_api.infra.worker.CompileFutureStore;
import com.sooscode.sooscode_api.infra.worker.CompileWorkerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class CompileServiceImpl implements CompileService {

    private final CompileWorkerClient compileWorkerClient;
    private final CompileFutureStore compileFutureStore;

    @Value("${app.urls.backend}")
    private String backendUrl;

    @Override
    @RequestCooldown(seconds = 3)
    public CompletableFuture<CompileResultResponse> runCode(String code) {

        // 유효성 검사
        CodeValidator.validateAll(code);
        /**
         *  blacklist 필터
         */
        CodeBlacklistFilter.validate(code);

        //job Id 생성
        String jobId = UUID.randomUUID().toString();


        try {
            /**
             *  callback URL 구성 및 Future 생성
             * - createFuture(jobId)는 "비어있는 Future" 만드는 시점
             * - 실제 thenApply 실행 스레드는 컨트롤러에서 지정해야 한다.
             */
            String callbackUrl =
                    backendUrl + "/api/compile/callback/" + jobId;
            CompletableFuture<CompileResultResponse> future =
                    compileFutureStore.createFuture(jobId);
            /**
             *  컴파일 워커 서버로 실행 요청
             *  이 시점에서  run  메서드 종료,  HTTP 스레드는 반환 준비임
             * */
            compileWorkerClient.requestCompile(jobId, code, callbackUrl);

            /**
             *  future 반환하고  callback 에서 result 받아야 완료됨.
             * */
            return future;

        } catch (CustomException exception) {
            throw exception;
        } catch (Exception exception) {
            compileFutureStore.failFuture(jobId, new CustomException(CompileStatus.WORKER_UNAVAILABLE));
                throw new CustomException(CompileStatus.WORKER_UNAVAILABLE);
        }
    }
}

