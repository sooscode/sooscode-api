package com.sooscode.sooscode_api.application.mypage.service;

import com.sooscode.sooscode_api.application.mypage.dto.MypageClassDetailResponse;
import org.springframework.stereotype.Service;

@Service
public interface MypageClassService {

    /** 클래스 상세 정보 조회 */
    MypageClassDetailResponse getClassDetail(Long classId);

    // 클래스 룸 엔티티에서 유저 아이디로 조회(강사) = 클래스 리스트 반환
    // ClassParticipant 여기서 유저아이디 검색(학생) = 클래스 ID 조회해서 리스트 반환
}
