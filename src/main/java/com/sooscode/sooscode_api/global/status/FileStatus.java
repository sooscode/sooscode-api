package com.sooscode.sooscode_api.global.status;

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
    PUBLIC_URL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "FILE_012", "해당 파일은 Public URL을 지원하지 않습니다"),
    REQUIRED(HttpStatus.BAD_REQUEST, "FILE_013", "업로드할 파일이 필요합니다"),
    EMPTY(HttpStatus.BAD_REQUEST, "FILE_014", "파일이 비어 있습니다"),
    NAME_INVALID(HttpStatus.BAD_REQUEST, "FILE_015", "파일 이름이 유효하지 않습니다"),
    EXTENSION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "FILE_016", "허용되지 않은 파일 확장자입니다"),
    COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "FILE_017", "파일 업로드 개수 제한을 초과했습니다"),
    DATE_REQUIRED(HttpStatus.BAD_REQUEST, "FILE_018", "업로드 날짜는 필수값입니다"),
    DATE_FORMAT_INVALID(HttpStatus.BAD_REQUEST, "FILE_019", "업로드 날짜 형식이 유효하지 않습니다"),
    INVALID_CLASS_ID(HttpStatus.BAD_REQUEST, "FILE_020", "유효하지 않은 클래스 ID입니다"),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "FILE_021", "파일 업로드 접근 권한이 없습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
