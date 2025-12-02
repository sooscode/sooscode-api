package com.sooscode.sooscode_api.application.compile.service;

import com.sooscode.sooscode_api.application.compile.dto.CompileResultResponse;
import com.sooscode.sooscode_api.application.compile.dto.CompileRunRequest;
import com.sooscode.sooscode_api.application.compile.dto.CompileRunResponse;

public interface CompileService {

    CompileRunResponse runCode(CompileRunRequest request);

    CompileResultResponse getCompileResult(String jobId);
}