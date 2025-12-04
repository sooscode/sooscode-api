package com.sooscode.sooscode_api.application.compile.controller;

import com.sooscode.sooscode_api.application.compile.dto.CompileResultResponse;
import com.sooscode.sooscode_api.application.compile.dto.CompileRunRequest;
import com.sooscode.sooscode_api.application.compile.service.CompileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compile")
@RequiredArgsConstructor
public class CompileController {

    private final CompileService compileService;

    /**
     *  post 코드 실행 run 요청  result 반환
     **/
    @PostMapping("/run")
    public ResponseEntity<CompileResultResponse> run(@Valid @RequestBody CompileRunRequest request) {
        CompileResultResponse result = compileService.runCode(request.getCode());


        return ResponseEntity.ok(result);
    }
}