package com.sooscode.sooscode_api.global.exception.errorcode;

import com.sooscode.sooscode_api.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SnapshotErrorCode implements ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "SNAPSHOT_001", "스냅샷을 찾을 수 없습니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN,"SNAPSHOT_002","작성한 사용자만 수정할 수 있습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}