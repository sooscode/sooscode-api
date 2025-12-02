package com.sooscode.sooscode_api.application.compile.service;

import com.sooscode.sooscode_api.application.compile.dto.CompileResultResponse;
import com.sooscode.sooscode_api.application.compile.dto.CompileRunRequest;
import com.sooscode.sooscode_api.application.compile.dto.CompileRunResponse;
import com.sooscode.sooscode_api.infra.worker.CompileWorkerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompileServiceImpl implements CompileService {

    private  final  CompileWorkerClient compileWorkerClient;

    @Override
    public CompileRunResponse runCode(CompileRunRequest request) {
        return compileWorkerClient.requestCompile(request.getCode());
    }

    @Override
    public CompileResultResponse getCompileResult(String jobId) {
        return compileWorkerClient.getCompileResult(jobId);
    }
}