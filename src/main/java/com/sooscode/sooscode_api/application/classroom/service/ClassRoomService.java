package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.ClassRoomDetailResponse;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ClassRoomService {
    /**
     * 클래스룸 상세 정보 조회 및 접속 권한 검증
     * 사용자가 특정 클래스룸에 접속할 수 있는지 권한을 검증하고,
     * 접속 가능한 경우 클래스룸의 상세 정보를 반환합니다.
     *
     * 접속 권한: 클래스룸을 생성한 강사 또는 클래스룸에 등록된 참가자
     *
     * @param classId 조회할 클래스룸의 ID
     * @param userId 접속을 시도하는 사용자의 ID
     * @return 클래스룸 상세 정보 (제목, 설명, 일정, 상태, 강사 정보, 파일 정보, 접속자 권한)
     * @throws CustomException CLASS_NOT_FOUND - 클래스룸이 존재하지 않는 경우
     * @throws CustomException CLASS_NOT_ACTIVE - 클래스룸이 비활성화된 경우
     * @throws CustomException CLASS_ACCESS_DENIED - 접속 권한이 없는 경우 (강사도 아니고 참가자도 아닌 경우)
     */
    ClassRoomDetailResponse getClassRoomDetail(Long classId, Long userId);

    void updateThumbnail(Long classId, Long userId, MultipartFile thumbnail) throws IOException;
}