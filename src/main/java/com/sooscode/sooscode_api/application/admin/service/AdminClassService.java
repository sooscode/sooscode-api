package com.sooscode.sooscode_api.application.admin.service;

import com.sooscode.sooscode_api.application.admin.dto.AdminClassRequest;
import com.sooscode.sooscode_api.application.admin.dto.AdminClassResponse;
import com.sooscode.sooscode_api.global.api.exception.CustomException;

public interface AdminClassService {

    /**
     * 클래스 생성
     * 관리자가 새로운 클래스를 생성합니다.
     * 시작/종료 시간을 검증하고, 파일이 있다면 연결합니다.
     *
     * @param request 클래스 생성 요청 DTO
     * @return 생성된 클래스 상세 정보
     * @throws CustomException FILE_NOT_FOUND - 파일(썸네일)을 찾을 수 없는 경우
     * @throws CustomException BAD_REQUEST - 시작/종료 시간을 정상적으로 생성 않은 경우
     */
    AdminClassResponse.Detail createClass(AdminClassRequest.Create request);

    /**
     * 클래스 수정
     * 기존 클래스의 제목, 설명, 시작/종료 시간을 수정합니다.
     *
     * @param classId 수정할 클래스 ID
     * @param request 클래스 수정 요청 DTO
     * @return 수정된 클래스 상세 정보
     * @throws CustomException CLASS_NOT_FOUND - 클래스를 찾을 수 없는 경우
     * @throws CustomException BAD_REQUEST - 시작/종료 시간을 정상적으로 수정하지 않은 경우
     */
    AdminClassResponse.Detail updateClass(Long classId, AdminClassRequest.Update request);

    /**
     * 클래스 삭제 (Soft Delete) -> isActive 비활성화
     * 클래스를 비활성화 처리합니다. 진행 중인 클래스는 삭제할 수 없습니다.
     *
     * @param classId 삭제할 클래스 ID
     * @throws CustomException CLASS_NOT_FOUND - 클래스를 찾을 수 없는 경우
     * @throws CustomException CLASS_STATUS_INVALID - 진행 중인 클래스인 경우
     */
    void deleteClass(Long classId);

    /**
     * 클래스 목록 조회
     * 페이지네이션, 필터링, 정렬을 지원하는 클래스 목록을 조회합니다.
     *
     * @param filter 검색 필터 (키워드, 상태, 날짜 범위 등)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 페이지네이션된 클래스 목록
     */
    AdminClassResponse.PageResponse getClassList(
            AdminClassRequest.SearchFilter filter,
            int page,
            int size
    );

    /**
     * 클래스 상세 조회
     * 특정 클래스의 상세 정보를 조회합니다.
     * 강사 이름, 참가자 수 등의 추가 정보를 포함합니다.
     *
     * @param classId 조회할 클래스 ID
     * @return 클래스 상세 정보
     * @throws CustomException CLASS_NOT_FOUND - 클래스를 찾을 수 없는 경우
     */
    AdminClassResponse.Detail getClassDetail(Long classId);

    /**
     * 강사 배정
     * 특정 클래스에 강사를 배정합니다.
     *
     * @param classId 클래스 ID
     * @param request 강사 배정 요청 DTO
     * @throws CustomException CLASS_NOT_FOUND - 클래스를 찾을 수 없는 경우
     * @throws CustomException USER_NOT_FOUND - 사용자를 찾을 수 없는 경우
     * @throws CustomException FORBIDDEN - 강사 권한이 없는 경우
     * @throws CustomException BAD_REQUEST - 이미 배정된 강사인 경우
     */
    void assignInstructor(Long classId, AdminClassRequest.AssignInstructor request);

    /**
     * 학생 일괄 배정
     * 특정 클래스에 여러 학생을 한번에 배정합니다.
     *
     * @param classId 클래스 ID
     * @param request 학생 배정 요청 DTO (학생 ID 리스트)
     * @throws CustomException CLASS_NOT_FOUND - 클래스를 찾을 수 없는 경우
     * @throws CustomException USER_NOT_FOUND - 학생을 찾을 수 없는 경우
     * @throws CustomException FORBIDDEN - 학생 권한이 없는 경우
     */
    void assignStudents(Long classId, AdminClassRequest.AssignStudents request);

    /**
     * 참여 학생 목록 조회
     * 특정 클래스에 배정된 학생 목록을 조회합니다.
     *
     * @param classId 클래스 ID
     * @return 참여 학생 목록
     * @throws CustomException CLASS_NOT_FOUND - 클래스를 찾을 수 없는 경우
     */
    //AdminClassResponse.StudentListResponse getStudentList(Long classId);
}