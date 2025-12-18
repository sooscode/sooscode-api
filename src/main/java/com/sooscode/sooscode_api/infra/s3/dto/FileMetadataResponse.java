package com.sooscode.sooscode_api.infra.s3.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * 파일 메타데이터 조회 DTO
 */
@Builder
@Data
public class FileMetadataResponse {
    private final Long fileId;
    private final String fileName;
    private final String fileUrl;
    private final String fileType;
    private final LocalDateTime createdAt;
}
