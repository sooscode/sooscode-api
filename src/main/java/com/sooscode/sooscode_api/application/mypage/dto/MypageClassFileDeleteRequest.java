package com.sooscode.sooscode_api.application.mypage.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MypageClassFileDeleteRequest {

    private Long teacherId;          // 컨트롤러에서 세팅
    private List<Long> fileIds;      // 삭제할 파일 ID 리스트
}
