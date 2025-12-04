package com.sooscode.sooscode_api.application.compile.service;

import com.sooscode.sooscode_api.application.compile.dto.CompileResultResponse;
import com.sooscode.sooscode_api.application.compile.dto.CompileRunRequest;
import com.sooscode.sooscode_api.application.compile.dto.CompileRunResponse;

public interface CompileService {

    /** 코드 실행 요청을 워커 서버로 전달하고 result를 반환한다. */
    CompileResultResponse runCode(String code);

}