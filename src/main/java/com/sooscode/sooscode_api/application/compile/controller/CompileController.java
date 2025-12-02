package com.sooscode.sooscode_api.application.compile.controller;

import com.sooscode.sooscode_api.application.compile.dto.CompileResultResponse;
import com.sooscode.sooscode_api.application.compile.dto.CompileRunRequest;
import com.sooscode.sooscode_api.application.compile.dto.CompileRunResponse;
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

    @PostMapping("/run")
    public ResponseEntity<CompileRunResponse> run(@Valid @RequestBody CompileRunRequest request) {
        return ResponseEntity.ok(compileService.runCode(request));
    }

    @GetMapping("/results/{jobId}")
    public ResponseEntity<CompileResultResponse> getResult(@PathVariable String jobId) {
        return ResponseEntity.ok(compileService.getCompileResult(jobId));
    }
}