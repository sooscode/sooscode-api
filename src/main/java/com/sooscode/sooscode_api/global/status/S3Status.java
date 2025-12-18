package com.sooscode.sooscode_api.global.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum S3Status implements StatusCode {

    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3_001", "파일 업로드에 실패했습니다"),
    DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3_002", "파일 삭제에 실패했습니다"),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "S3_003", "파일을 찾을 수 없습니다"),
    CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3_004", "S3 서버 연결에 실패했습니다"),
    INVALID_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "S3_005", "S3 응답이 유효하지 않습니다"),
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "S3_006", "업로드할 파일이 비어있습니다"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "S3_007", "허용되지 않는 파일 형식입니다"),
    PRESIGNED_URL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3_008", "Presigned URL 생성에 실패했습니다"),
    PUBLIC_URL_NOT_ALLOWED(HttpStatus.FORBIDDEN, "S3_009", "해당 파일은 Public URL을 제공하지 않습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}