package com.sooscode.sooscode_api.application.snapshot.dto;

import com.sooscode.sooscode_api.domain.snapshot.entity.CodeSnapshot;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SnapshotSaveResponse {
    private Long snapshotId;
    private String title;
    private String content;
    public static SnapshotSaveResponse from(CodeSnapshot snapshot) {
        return new SnapshotSaveResponse(
                snapshot.getCodeSnapshotId(),
                snapshot.getTitle(),
                snapshot.getContent()
        );
    }
}
