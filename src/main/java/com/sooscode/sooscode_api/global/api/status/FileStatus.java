package com.sooscode.sooscode_api.global.api.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileStatus implements StatusCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "FILE_001", "파일을 찾을 수 없습니다"),
    METADATA_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_002", "파일 메타데이터 저장 실패"),
    METADATA_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_003", "파일 메타데이터 수정 실패"),
    METADATA_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_004", "파일 메타데이터 삭제 실패"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "FILE_005", "잘못된 파일 요청입니다"),

    // ClassRoom File 관련 ErrorCode 추가 ( kinhyo )
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_006", "파일 업로드에 실패했습니다"),
    DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_007", "파일 삭제에 실패했습니다"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "FILE_008", "허용되지 않은 파일 형식입니다"),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "FILE_009", "파일 크기가 허용 범위를 초과했습니다"),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "FILE_010", "파일에 접근할 권한이 없습니다"),
    PRESIGNED_URL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_011", "파일 다운로드 URL 생성에 실패했습니다"),
    PUBLIC_URL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "FILE_012", "해당 파일은 Public URL을 지원하지 않습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
