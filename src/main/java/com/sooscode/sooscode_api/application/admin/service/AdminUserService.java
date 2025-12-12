package com.sooscode.sooscode_api.application.admin.service;

import com.sooscode.sooscode_api.application.admin.dto.AdminUserRequest;
import com.sooscode.sooscode_api.application.admin.dto.AdminUserResponse;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.global.api.exception.CustomException;

import java.util.List;

/**
 * 관리자용 사용자 관리 서비스
 * 사용자 생성, 조회, 수정, 삭제 및 통계 기능을 제공합니다.
 */
public interface AdminUserService {

    /**
     * 강사 계정 생성
     * 관리자가 직접 강사 계정을 생성하고 임시 비밀번호를 발급합니다.
     * 생성된 계정 정보는 이메일로 전송됩니다.
     *
     * @param request 강사 생성 요청 DTO (이메일, 이름 등)
     * @return 생성된 강사 정보 및 임시 비밀번호
     * @throws CustomException AUTH_DUPLICATE_EMAIL - 이미 가입된 이메일인 경우
     * @throws CustomException BAD_REQUEST - 유효하지 않은 입력값인 경우
     */
    AdminUserResponse.InstructorCreated createUser(AdminUserRequest.Create request, UserRole role);

    /**
     * 전체 사용자 목록 조회
     * 페이지네이션, 필터링, 검색을 지원합니다.
     * 역할(강사/학생/관리자), 기간, 계정 상태별 필터링이 가능합니다.
     *
     * @param filter 검색 및 필터 조건
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 페이지네이션된 사용자 목록
     */
//    AdminUserResponse.PageResponse getUserList(
//            AdminUserRequest.SearchFilter filter,
//            int page,
//            int size
//    );

    /**
     * 사용자 상세 정보 조회
     * 특정 사용자의 상세 정보를 조회합니다.
     * 기본 정보, 최근 로그인 히스토리, 참여 클래스 정보를 포함합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 상세 정보
     * @throws CustomException USER_NOT_FOUND - 사용자를 찾을 수 없는 경우
     */
    AdminUserResponse.Detail getUserDetail(Long userId);

    /**
     * 사용자 로그인 히스토리 조회
     * 최근 N회의 로그인 일시 및 IP 주소를 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @param limit 조회할 개수 (기본 5개)
     * @return 로그인 히스토리 목록
     * @throws CustomException USER_NOT_FOUND - 사용자를 찾을 수 없는 경우
     */
    List<AdminUserResponse.LoginHistory> getLoginHistory(Long userId, int limit);

    /**
     * 사용자 계정 삭제 (Soft Delete)
     * 사용자 계정을 비활성화 처리합니다.
     * 실제 데이터는 삭제되지 않고 상태만 변경됩니다.
     *
     * @param userId 삭제할 사용자 ID
     * @throws CustomException USER_NOT_FOUND - 사용자를 찾을 수 없는 경우
     * @throws CustomException FORBIDDEN - 관리자 계정을 삭제하려는 경우
     */
    void deleteUser(Long userId);

    /**
     * 사용자 계정 활성화/비활성화
     * 사용자의 계정 상태를 변경합니다.
     *
     * @param userId 사용자 ID
     * @param isActive 활성화 여부 (true: 활성화, false: 비활성화)
     * @throws CustomException USER_NOT_FOUND - 사용자를 찾을 수 없는 경우
     */
    void toggleUserStatus(Long userId, boolean isActive);

    /**
     * 사용자 역할 변경
     * 사용자의 역할(강사/학생)을 변경합니다.
     * 관리자 역할로는 변경할 수 없습니다.
     *
     * @param userId 사용자 ID
     * @param request 역할 변경 요청 DTO
     * @throws CustomException USER_NOT_FOUND - 사용자를 찾을 수 없는 경우
     * @throws CustomException FORBIDDEN - 관리자 역할로 변경하려는 경우
     */
    void changeUserRole(Long userId, AdminUserRequest.ChangeRole request);

    /**
     * 일괄 계정 생성 (CSV)
     * CSV 파일을 업로드하여 여러 계정을 동시에 생성합니다.
     * 각 계정에 대해 임시 비밀번호가 자동 발급됩니다.
     *
     * @param request CSV 파일 업로드 요청
     * @return 생성 결과 (성공/실패 건수 및 상세 내역)
     * @throws CustomException FILE_TYPE_NOT_ALLOWED - CSV 파일이 아닌 경우
     * @throws CustomException BAD_REQUEST - CSV 형식이 올바르지 않은 경우
     */
    AdminUserResponse.BulkCreateResult bulkCreateUsers(AdminUserRequest.BulkCreate request);

    /**
     * 사용자 데이터 엑셀 다운로드
     * 전체 또는 필터링된 사용자 목록을 엑셀 파일로 생성합니다.
     *
     * @param filter 검색 및 필터 조건
     * @return 엑셀 파일 바이트 배열
     */
    byte[] exportUsersToExcel(AdminUserRequest.SearchFilter filter);

    /**
     * 대시보드용 사용자 통계 조회
     * 전체 사용자 수, 역할별 사용자 수, 최근 가입자 수 등을 조회합니다.
     *
     * @return 사용자 통계 정보
     */
    AdminUserResponse.Statistics getUserStatistics();
}