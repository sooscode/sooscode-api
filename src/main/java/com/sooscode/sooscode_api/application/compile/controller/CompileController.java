package com.sooscode.sooscode_api.application.compile.controller;

import com.sooscode.sooscode_api.application.compile.dto.CompileResultResponse;
import com.sooscode.sooscode_api.application.compile.dto.CompileRunRequest;
import com.sooscode.sooscode_api.application.compile.service.CompileService;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.response.ApiResponse;
import com.sooscode.sooscode_api.global.status.CompileStatus;
import com.sooscode.sooscode_api.infra.worker.CompileFutureStore;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@RestController
@RequestMapping("/api/compile")
@RequiredArgsConstructor
public class CompileController {

    private final CompileService compileService;
    private final CompileFutureStore compileFutureStore;
    private final Executor securityAsyncExecutor;

    /**
     *  post 코드 실행 run 요청  result 반환
     **/
    @PostMapping("/run")
    public CompletableFuture<ResponseEntity<ApiResponse<CompileResultResponse>>> run(
            @Valid @RequestBody CompileRunRequest request){
        log.info(" /run 요청 수신");

        /**
         *  비동기 컴파일 요청 Future 획득 시점
         * - 여기서는 future 만 반환, 실제 실행은 콜백이 도착해야 완료
         * - 현재 HTTP 스레드에서는 SecurityContext가 존재함 (인증된 상태).
         */
        try {
            String decode = new  String(
                    Base64.getDecoder().decode(request.getCode()),
                    StandardCharsets.UTF_8
            );

            CompletableFuture<CompileResultResponse> resultFuture =
                    compileService.runCode(decode);
            log.info(" Future 생성 완료");

            /**
             *  비동기 처리 thenApply 가 토큰 인증정보를 가지고있지않음
             * - 기본 thenApply 사용 시, ForkJoinPool(common pool)에서 실행
             * - 해당 스레드에는 SecurityContext가 존재하지 않음 → 401 발생
             * - 따라서 SecurityAsyncExecutor를 이용하여 인증 정보를 전달한 상태로 처리
             */
//        return resultFuture.thenApplyAsync(
//                ResponseEntity::ok, securityAsyncExecutor);
            return resultFuture.thenApplyAsync(
                    result -> ApiResponse.ok(CompileStatus.OK, result),
                    securityAsyncExecutor
            );

        }catch (CustomException cexception){
            return CompletableFuture.failedFuture(cexception);
        }
        /**
         *  원래는  securityAsyncExecutor를 같이 return 하지 않았음
         */
    }

    /**
     *  컴파일 서버로부터 컴파일 결과를 수신하는 콜백
     */
    @PostMapping("/callback/{jobId}")
    public ResponseEntity<ApiResponse<Void>> receiveCallback(
            @PathVariable String jobId,
            @RequestBody CompileResultResponse result) {
        log.info("[Callback] 결과 수신 jobId={}, result={}", jobId, result);

        // 워커로부터 결과를 받으면 Future Store의 대기 중인 퓨처를 완료
        compileFutureStore.completeFuture(jobId, result);

        // 워커 서버 에서 받음
        return ApiResponse.ok(CompileStatus.OK, null);
    }
}