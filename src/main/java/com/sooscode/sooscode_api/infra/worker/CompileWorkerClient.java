package com.sooscode.sooscode_api.infra.worker;

import com.sooscode.sooscode_api.application.compile.dto.CompileResultResponse;
import com.sooscode.sooscode_api.application.compile.dto.CompileRunResponse;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompileWorkerClient {

    private final RestTemplate restTemplate = createRestTemplate();

    @Value("${compile.worker-url}")
    private String workerUrl;

    /**
     * [POST] 워커에게 실행 요청
     */
    public CompileRunResponse requestCompile(String code) {


        String targetUrl = workerUrl + "/api/compile/run";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("code", code);

        try {
            ResponseEntity<CompileRunResponse> response =
                    restTemplate.postForEntity(targetUrl, requestBody, CompileRunResponse.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("워커 서버 통신 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.CODE_SERVER_CONNECTION_FAILED);
        }
    }
    /**
     * [GET] 워커에게 결과 조회
     */
    public CompileResultResponse getCompileResult(String jobId) {
        String targetUrl = workerUrl + "/api/compile/result/" + jobId;

        try {
            return restTemplate.getForObject(targetUrl, CompileResultResponse.class);
        } catch (Exception e) {
            log.error("결과 조회 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.CODE_SERVER_CONNECTION_FAILED);
        }
    }
    /**
     * 내부적으로 RestTemplate 생성 (타임아웃 적용)
     */
    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(7000); //7초
        factory.setReadTimeout(15000);    // 15초
        return new RestTemplate(factory);
    }
}