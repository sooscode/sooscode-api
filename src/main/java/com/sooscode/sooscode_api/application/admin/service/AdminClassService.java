package com.sooscode.sooscode_api.application.admin.service;

import com.sooscode.sooscode_api.application.admin.dto.AdminClassRequest;
import com.sooscode.sooscode_api.application.admin.dto.AdminClassResponse;
import com.sooscode.sooscode_api.application.admin.dto.AdminPageResponse;
import com.sooscode.sooscode_api.global.exception.CustomException;

import java.util.List;

public interface AdminClassService {

    /**
     * 클래스 생성
     * 관리자가 새로운 클래스를 생성
     * 시작/종료 시간을 검증하고, 파일이 있다면 연결
     *
     * @param request 클래스 생성 요청 DTO
     * @return 생성된 클래스 상세 정보
     * @throws CustomException FILE_NOT_FOUND - 파일(썸네일)을 찾을 수 없는 경우
     * @throws CustomException BAD_REQUEST - 시작/종료 시간을 정상적으로 생성 않은 경우
     */
    AdminClassResponse.ClassItem createClass(AdminClassRequest.Create request);

    /**
     * 클래스 수정
     * 기존 클래스의 제목, 설명, 강사, 시작/종료 시간을 수정
     *
     * @param classId 수정할 클래스 ID
     * @param request 클래스 수정 요청 DTO
     * @return 수정된 클래스 상세 정보
     * @throws CustomException CLASS_NOT_FOUND - 클래스를 찾을 수 없는 경우
     * @throws CustomException BAD_REQUEST - 시작/종료 시간을 정상적으로 수정하지 않은 경우
     */
    AdminClassResponse.ClassItem updateClass(Long classId, AdminClassRequest.Update request);

    /**
     * 클래스 삭제 (Soft Delete) -> isActive 비활성화
     * 클래스를 비활성화 처리, 진행 중인 클래스는 삭제할 수 없음
     *
     * @param classId 삭제할 클래스 ID
     * @throws CustomException CLASS_NOT_FOUND - 클래스를 찾을 수 없는 경우
     * @throws CustomException CLASS_STATUS_INVALID - 진행 중인 클래스인 경우
     */
    void deleteClass(Long classId);

    /**
     * 클래스 상세 조회
     * 특정 클래스의 상세 정보를 조회합니다.
     * 강사 이름, 참가자 수 등의 추가 정보를 포함합니다.
     *
     * @param classId 조회할 클래스 ID
     * @return 클래스 상세 정보
     * @throws CustomException CLASS_NOT_FOUND - 클래스를 찾을 수 없는 경우
     */
    AdminClassResponse.ClassItem getClassDetail(Long classId);

    /**
     * 학생 일괄 배정
     * 특정 클래스에 여러 학생을 한번에 배정
     *
     * @param classId 클래스 ID
     * @param request 학생 배정 요청 DTO (학생 ID 리스트)
     * @return 각 학생별 배정 결과
     * @throws CustomException CLASS_NOT_FOUND - 클래스를 찾을 수 없는 경우
     * @throws CustomException USER_NOT_FOUND - 학생을 찾을 수 없는 경우
     * @throws CustomException FORBIDDEN - 학생 권한이 없는 경우
     */
    AdminClassResponse.StudentOperationResponse assignStudents(Long classId, AdminClassRequest.Students request);

    /**
     * 학생 일괄 삭제
     * 특정 클래스에 여러 학생을 한번에 삭제
     *
     * @param classId 클래스 ID
     * @param request 학생 배정 삭제 DTO (학생 ID 리스트)
     * @return 각 학생별 배정 해제 결과
     * @throws CustomException CLASS_NOT_FOUND - 클래스를 찾을 수 없는 경우
     * @throws CustomException USER_NOT_FOUND - 학생을 찾을 수 없는 경우
     * @throws CustomException FORBIDDEN - 학생 권한이 없는 경우
     */
    AdminClassResponse.StudentOperationResponse deleteStudents(Long classId, AdminClassRequest.Students request);

    /**
     * 참여 학생 목록 조회
     * 특정 클래스에 배정된 학생 목록을 조회합니다.
     *
     * @return 참여 학생 목록
     * @throws CustomException CLASS_NOT_FOUND - 클래스를 찾을 수 없는 경우
     */
    AdminPageResponse<AdminClassResponse.ClassStudentsResponse> getClassStudentsList(Long classId, AdminClassRequest.SearchFilter filter, int page, int pageSize);

    /**
     * 클래스 목록 조회 (페이지네이션 + 필터링)
     * 검색 키워드(제목, 강사명), 상태, 날짜 범위로 필터링하고 정렬하여 페이지 단위로 조회
     *
     * @param filter 검색 필터 (keyword: 제목/강사명 검색, status: 클래스 상태, startDate/endDate: 날짜 범위, sortBy/sortDirection: 정렬 조건)
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지당 항목 수
     * @return 페이지네이션된 클래스 목록 (content, currentPage, totalPages, totalElements, size)
     */
    AdminPageResponse<AdminClassResponse.ClassItem> getClassList(AdminClassRequest.SearchFilter filter, int page, int size);

    /**
     * 강사 검색
     * 이름 또는 이메일로 활성 상태의 강사 목록 조회
     *
     * @param keyword 검색 키워드 (이름 또는 이메일)
     * @return 강사 목록 (userId, name, email)
     */
    List<AdminClassResponse.UserSearchItem> searchInstructors(String keyword);

    /**
     * 클래스에 속하지 않은 학생 검색
     * 특정 클래스에 참여하지 않은 활성 상태의 학생 목록 조회
     *
     * @param classId 클래스 ID
     * @param keyword 검색 키워드 (이름 또는 이메일)
     * @return 학생 목록 (userId, name, email)
     */
    List<AdminClassResponse.UserSearchItem> searchAvailableStudents(Long classId, String keyword);

    /**
     * 클래스 정보 및 수강생 목록 엑셀 다운로드
     * 클래스 기본 정보와 수강생 명단을 엑셀 파일로 생성
     *
     * @param classId 클래스 ID
     * @return 엑셀 파일 바이트 배열
     * @throws CustomException CLASS_NOT_FOUND - 클래스를 찾을 수 없는 경우
     */
    byte[] exportClassToExcel(Long classId);

    /**
     * 전체 클래스 목록 엑셀 다운로드
     * 필터 조건에 맞는 클래스 목록을 엑셀 파일로 생성
     *
     * @param filter 검색 필터
     * @return 엑셀 파일 바이트 배열
     */
    byte[] exportClassListToExcel(AdminClassRequest.SearchFilter filter);
}