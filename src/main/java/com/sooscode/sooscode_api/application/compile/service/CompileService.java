package com.sooscode.sooscode_api.application.compile.service;

import com.sooscode.sooscode_api.application.compile.dto.CompileResultResponse;
import com.sooscode.sooscode_api.application.compile.dto.CompileRunRequest;
import com.sooscode.sooscode_api.application.compile.dto.CompileRunResponse;

public interface CompileService {
    /** 코드 실행 요청을 워커 서버로 전달하고 jobId를 반환한다. */
    CompileRunResponse runCode(CompileRunRequest request);

    /** jobid를 가지고 워커 서버에 저장된 코드 실행 결과를 조회한다. */
    CompileResultResponse getCompileResult(String jobId);
}