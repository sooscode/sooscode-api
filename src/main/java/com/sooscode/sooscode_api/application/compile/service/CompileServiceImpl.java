package com.sooscode.sooscode_api.application.compile.service;

import com.sooscode.sooscode_api.application.compile.dto.CompileResultResponse;
import com.sooscode.sooscode_api.application.compile.dto.CompileRunResponse;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.ErrorCode;
import com.sooscode.sooscode_api.infra.worker.CodeBlacklistFilter;
import com.sooscode.sooscode_api.infra.worker.CompileWorkerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompileServiceImpl implements CompileService {

    private  final  CompileWorkerClient compileWorkerClient;

    @Override
    public CompileResultResponse runCode(String code) {

        // 코드 유효성 검사 (블랙리스트 등)
        CodeBlacklistFilter.validate(code);

        // 실행 요청 → jobId 획득
        CompileRunResponse runResponse = compileWorkerClient.requestCompile(code);

        String jobId = runResponse.getJobId();
        if (jobId == null || jobId.isBlank()) {
            throw new CustomException(ErrorCode.CODE_SERVER_CONNECTION_FAILED);
        }
        //  결과 polling
        for (int i = 0; i < 30; i++) { // 30 * 500ms = 15초
            CompileResultResponse result = compileWorkerClient.getCompileResult(jobId);
            // PENDING 아니면 즉시 반환
            if (!"PENDING".equals(result.getStatus())) {
                return result;
            }
            // 대기 후 재시도
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        }
        // 4. timeout
        return new CompileResultResponse("TIMEOUT", "Execution timed out");
    }
}