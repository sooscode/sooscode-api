package com.sooscode.sooscode_api.global.api.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SnapshotStatus implements StatusCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "SNAPSHOT_001", "스냅샷을 찾을 수 없습니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN,"SNAPSHOT_002","작성한 사용자만 수정할 수 있습니다"),
    CONTENT_EMPTY(HttpStatus.BAD_REQUEST, "SNAPSHOT_003", "내용을 입력해주세요"),
    LIST_EMPTY(HttpStatus.BAD_REQUEST, "SNAPSHOT_004", "데이터 정보가 없습니다"),
    CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "SNAPSHOT_005", "내용은 50자 이하여야 합니다"),
    TITLE_EMPTY(HttpStatus.BAD_REQUEST, "SNAPSHOT_006", "제목을 입력해주세요"),
    TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "SNAPSHOT_007", "제목은 50자 이하여야 합니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}