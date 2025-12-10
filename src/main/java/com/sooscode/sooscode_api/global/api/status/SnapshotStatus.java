package com.sooscode.sooscode_api.global.api.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SnapshotStatus implements StatusCode {

    OK(HttpStatus.OK, "SNAPSHOT_000", "스냅샷이 성공적으로 입력되었습니다"),
    UPDATE_OK(HttpStatus.OK, "SNAPSHOT_001", "스냅샷이 성공적으로 수정되었습니다"),
    DELETE_OK(HttpStatus.OK, "SNAPSHOT_002", "스냅샷이 성공적으로 삭제되었습니다"),
    READ_OK(HttpStatus.OK, "SNAPSHOT_003", "스냅샷이 성공적으로 조회되었습니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "SNAPSHOT_004", "스냅샷을 찾을 수 없습니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN,"SNAPSHOT_005","작성한 사용자만 수정할 수 있습니다"),
    CONTENT_EMPTY(HttpStatus.BAD_REQUEST, "SNAPSHOT_006", "내용을 입력해주세요"),
    LIST_EMPTY(HttpStatus.BAD_REQUEST, "SNAPSHOT_007", "데이터 정보가 없습니다"),
    CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, "SNAPSHOT_008", "내용은 50자 이하여야 합니다"),
    TITLE_EMPTY(HttpStatus.BAD_REQUEST, "SNAPSHOT_009", "제목을 입력해주세요"),
    TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "SNAPSHOT_010", "제목은 50자 이하여야 합니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}