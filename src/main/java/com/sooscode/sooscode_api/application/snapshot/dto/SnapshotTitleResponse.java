package com.sooscode.sooscode_api.application.snapshot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SnapshotTitleResponse {
    private Long codeSnapshotId;
    private String title;
}
