package com.sooscode.sooscode_api.application.mypage.service;

import com.sooscode.sooscode_api.application.mypage.dto.MypageClassDetailResponse;
import com.sooscode.sooscode_api.application.mypage.dto.MypageMyclassesResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface MypageClassService {

    /** 클래스 상세 정보 조회 */
    MypageClassDetailResponse getClassDetail(Long classId);

//    // 클래스 룸 엔티티에서 유저 아이디로 조회(강사) = 클래스 리스트 반환
//    List<MypageMyclassesResponse> getStudentClasses(Long userId);
//    // ClassParticipant 여기서 유저아이디 검색(학생) = 클래스 ID 조회해서 리스트 반환
//    List<MypageMyclassesResponse> getTeacherClasses(Long userId);

    Page<MypageMyclassesResponse> getStudentClasses(Long userId, Pageable pageable);

    Page<MypageMyclassesResponse> getTeacherClasses(Long userId, Pageable pageable);

    void updateThumbnail(Long classId, Long userId, MultipartFile thumbnail) throws IOException;
}
