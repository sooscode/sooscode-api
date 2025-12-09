package com.sooscode.sooscode_api.application.mypage.service;

//import com.sooscode.sooscode_api.application.classroom.dto.ClassDetailResponse;
import com.sooscode.sooscode_api.application.mypage.dto.ClassRoomResponse;
import org.springframework.stereotype.Service;

@Service
public interface MypageClassService {

    /** 클래스 상세 정보 조회 */
    ClassRoomResponse.Detail getClassDetail(Long classId);
}
