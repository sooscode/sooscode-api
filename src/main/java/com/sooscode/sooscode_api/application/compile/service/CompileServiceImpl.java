package com.sooscode.sooscode_api.application.compile.service;

import com.sooscode.sooscode_api.application.compile.dto.CompileResultResponse;
import com.sooscode.sooscode_api.application.compile.dto.CompileRunRequest;
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
    public CompileRunResponse runCode(CompileRunRequest request) {
        String code = request.getCode();
        CodeBlacklistFilter.validate(code);
        try{
            return compileWorkerClient.requestCompile(code);
        }catch(CustomException e){
            throw e;
        }catch(Exception e){
           throw new CustomException(ErrorCode.CODE_SERVER_CONNECTION_FAILED);
        }
    }

    @Override
    public CompileResultResponse getCompileResult(String jobId) {
        return compileWorkerClient.getCompileResult(jobId);
    }
}