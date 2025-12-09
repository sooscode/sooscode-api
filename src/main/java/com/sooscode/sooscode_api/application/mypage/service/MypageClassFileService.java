package com.sooscode.sooscode_api.application.mypage.service;

import com.sooscode.sooscode_api.application.mypage.dto.MypageClassFileDeleteRequest;
import com.sooscode.sooscode_api.application.mypage.dto.MypageClassFileUploadRequest;
import com.sooscode.sooscode_api.application.mypage.dto.ClassRoomFileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface MypageClassFileService {

    /** 클래스 자료 파일 업로드 */
    List<ClassRoomFileResponse> uploadFiles(MypageClassFileUploadRequest rq) throws Exception;

    /** 클래스 ID 기준으로 자료 파일 목록 조회 */
    Page<ClassRoomFileResponse> getFilesByClassId(Long classId, Pageable pageable);

    /** 특정 수업 날짜 기준으로 자료 파일 조회 */
    Page<ClassRoomFileResponse> getFilesByLectureDate(Long classId, LocalDate lectureDate, Pageable pageable);

    /** 업로드된 자료 파일 삭제 */
    void deleteFiles(MypageClassFileDeleteRequest rq) throws Exception;
}
