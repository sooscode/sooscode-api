package com.sooscode.sooscode_api.application.admin.service;

import com.sooscode.sooscode_api.application.admin.dto.AdminClassRequest;
import com.sooscode.sooscode_api.application.admin.dto.AdminClassResponse;
import com.sooscode.sooscode_api.application.admin.dto.AdminPageResponse;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.enums.ClassMode;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassParticipantRepository;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.AdminStatus;
import com.sooscode.sooscode_api.global.api.status.ClassRoomStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminClassServiceImpl implements AdminClassService {

    private final ClassRoomRepository classroomRepository;
    private final UserRepository userRepository;
    private final ClassParticipantRepository classParticipantRepository;

    @Override
    @Transactional
    public AdminClassResponse.ClassItem createClass(AdminClassRequest.Create request) {
        // 강사 검증
        User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new CustomException(AdminStatus.USER_NOT_FOUND));

        if (!instructor.getRole().equals(UserRole.INSTRUCTOR)) {
            throw new CustomException(AdminStatus.CLASS_INSTRUCTOR_INVALID);
        }

        // 클래스 생성
        ClassRoom classRoom = ClassRoom.builder()
                .isOnline(request.getIsOnline())
                .isActive(true)
                .user(instructor)
                .title(request.getTitle())
                .description(request.getDescription())
                .file(null)
                .mode(ClassMode.FREE)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        classroomRepository.save(classRoom);

        // 응답 생성
        Integer studentCount = 0;
        String thumbnail = null;
        String instructorName = (instructor != null) ? instructor.getName() : null;

        return AdminClassResponse.ClassItem.from(
                classRoom,
                thumbnail,
                instructorName,
                studentCount
        );
    }

    @Override
    @Transactional
    public AdminClassResponse.ClassItem updateClass(Long classId, AdminClassRequest.Update request) {
        // 클래스 조회
        ClassRoom classRoom = classroomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        // 강사 검증 및 변경
        User instructor = null;
        if (request.getInstructorId() != null) {
            instructor = userRepository.findById(request.getInstructorId())
                    .orElseThrow(() -> new CustomException(AdminStatus.USER_NOT_FOUND));

            // 강사 권한 확인 (원본 코드에 논리 오류가 있어서 수정)
            if (!instructor.getRole().equals(UserRole.INSTRUCTOR)) {
                throw new CustomException(AdminStatus.CLASS_INSTRUCTOR_INVALID);
            }
            classRoom.setUser(instructor);
        }

        classRoom.setTitle(request.getTitle());
        classRoom.setDescription(request.getDescription());
        classRoom.setOnline(request.getIsOnline());
        classRoom.setStartDate(request.getStartDate());
        classRoom.setEndDate(request.getEndDate());
        classRoom.setStartTime(request.getStartTime());
        classRoom.setEndTime(request.getEndTime());

        // 응답 생성
        List<ClassParticipant> participants = classParticipantRepository.findByClassRoom_ClassId(classId);
        Integer studentCount = participants.size();
        String thumbnail = null;
        String instructorName = classRoom.getUser() != null ? classRoom.getUser().getName() : null;

        return AdminClassResponse.ClassItem.from(
                classRoom,
                thumbnail,
                instructorName,
                studentCount
        );
    }

    @Override
    @Transactional
    public void deleteClass(Long classId) {
        // 클래스 조회
        ClassRoom classRoom = classroomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        // Soft Delete: isActive를 false로 변경
        classRoom.setActive(false);
    }

    @Override
    public AdminClassResponse.ClassItem getClassDetail(Long classId) {
        // 클래스 조회
        ClassRoom classRoom = classroomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        // 학생 수 조회
        Integer studentCount = classParticipantRepository.findByClassRoom_ClassId(classId).size();

        // 썸네일 경로
        String thumbnail = null;

        // 강사 이름
        String instructorName = classRoom.getUser() != null ? classRoom.getUser().getName() : null;

        return AdminClassResponse.ClassItem.from(classRoom, thumbnail, instructorName, studentCount);
    }

    @Override
    @Transactional
    public AdminClassResponse.StudentOperationResponse assignStudents(
            Long classId,
            AdminClassRequest.Students request) {

        // 클래스 조회
        ClassRoom classRoom = classroomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        List<ClassParticipant> newParticipants = new ArrayList<>();
        List<AdminClassResponse.StudentOperationResult> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (Long studentId : request.getStudentIds()) {
            AdminClassResponse.StudentOperationResult result;

            try {
                // 학생 조회
                User student = userRepository.findById(studentId)
                        .orElseThrow(() -> new CustomException(AdminStatus.USER_NOT_FOUND));

                // 학생 권한 확인
                if (!student.getRole().equals(UserRole.STUDENT)) {
                    result = AdminClassResponse.StudentOperationResult.builder()
                            .studentId(studentId)
                            .studentName(student.getName())
                            .success(false)
                            .message("학생 권한이 아닙니다")
                            .build();
                    failureCount++;
                    results.add(result);
                    continue;
                }

                // 이미 배정된 학생인지 확인
                Optional<ClassParticipant> existingParticipant = classParticipantRepository
                        .findByClassRoom_ClassIdAndUser_UserId(classId, studentId);

                if (existingParticipant.isPresent()) {
                    result = AdminClassResponse.StudentOperationResult.builder()
                            .studentId(studentId)
                            .studentName(student.getName())
                            .success(false)
                            .message("이미 배정된 학생입니다")
                            .build();
                    failureCount++;
                    results.add(result);
                    continue;
                }

                // 새로운 참여자 생성
                ClassParticipant participant = ClassParticipant.builder()
                        .user(student)
                        .classRoom(classRoom)
                        .build();
                newParticipants.add(participant);

                result = AdminClassResponse.StudentOperationResult.builder()
                        .studentId(studentId)
                        .studentName(student.getName())
                        .success(true)
                        .message("배정 성공")
                        .build();
                successCount++;
                results.add(result);

            } catch (CustomException e) {
                // 사용자를 찾을 수 없는 경우
                result = AdminClassResponse.StudentOperationResult.builder()
                        .studentId(studentId)
                        .studentName(null)
                        .success(false)
                        .message("존재하지 않는 사용자입니다")
                        .build();
                failureCount++;
                results.add(result);

            } catch (Exception e) {
                // 예상치 못한 오류
                result = AdminClassResponse.StudentOperationResult.builder()
                        .studentId(studentId)
                        .studentName(null)
                        .success(false)
                        .message("배정 중 오류가 발생했습니다: " + e.getMessage())
                        .build();
                failureCount++;
                results.add(result);
                log.error("학생 배정 중 오류 발생 - studentId: {}, error: {}", studentId, e.getMessage());
            }
        }

        // 일괄 저장
        if (!newParticipants.isEmpty()) {
            classParticipantRepository.saveAll(newParticipants);
            log.info("학생 일괄 배정 완료 - classId: {}, 성공: {}명, 실패: {}명",
                    classId, successCount, failureCount);
        }

        return AdminClassResponse.StudentOperationResponse.builder()
                .totalCount(request.getStudentIds().size())
                .successCount(successCount)
                .failureCount(failureCount)
                .results(results)
                .build();
    }

    @Override
    @Transactional
    public AdminClassResponse.StudentOperationResponse deleteStudents(
            Long classId,
            AdminClassRequest.Students request) {

        // 클래스 조회
        ClassRoom classRoom = classroomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        List<ClassParticipant> participantsToDelete = new ArrayList<>();
        List<AdminClassResponse.StudentOperationResult> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (Long studentId : request.getStudentIds()) {
            AdminClassResponse.StudentOperationResult result;

            try {
                // 학생 조회
                User student = userRepository.findById(studentId)
                        .orElseThrow(() -> new CustomException(AdminStatus.USER_NOT_FOUND));

                // 학생 권한 확인
                if (!student.getRole().equals(UserRole.STUDENT)) {
                    result = AdminClassResponse.StudentOperationResult.builder()
                            .studentId(studentId)
                            .studentName(student.getName())
                            .success(false)
                            .message("학생 권한이 아닙니다")
                            .build();
                    failureCount++;
                    results.add(result);
                    continue;
                }

                // 배정된 학생인지 확인
                Optional<ClassParticipant> existingParticipant = classParticipantRepository
                        .findByClassRoom_ClassIdAndUser_UserId(classId, studentId);

                if (existingParticipant.isEmpty()) {
                    result = AdminClassResponse.StudentOperationResult.builder()
                            .studentId(studentId)
                            .studentName(student.getName())
                            .success(false)
                            .message("배정되지 않은 학생입니다")
                            .build();
                    failureCount++;
                    results.add(result);
                    continue;
                }

                // 삭제 목록에 추가
                participantsToDelete.add(existingParticipant.get());

                result = AdminClassResponse.StudentOperationResult.builder()
                        .studentId(studentId)
                        .studentName(student.getName())
                        .success(true)
                        .message("배정 해제 성공")
                        .build();
                successCount++;
                results.add(result);

            } catch (CustomException e) {
                // 사용자를 찾을 수 없는 경우
                result = AdminClassResponse.StudentOperationResult.builder()
                        .studentId(studentId)
                        .studentName(null)
                        .success(false)
                        .message("존재하지 않는 사용자입니다")
                        .build();
                failureCount++;
                results.add(result);

            } catch (Exception e) {
                // 예상치 못한 오류
                result = AdminClassResponse.StudentOperationResult.builder()
                        .studentId(studentId)
                        .studentName(null)
                        .success(false)
                        .message("배정 해제 중 오류가 발생했습니다: " + e.getMessage())
                        .build();
                failureCount++;
                results.add(result);
                log.error("학생 배정 해제 중 오류 발생 - studentId: {}, error: {}", studentId, e.getMessage());
            }
        }

        // 일괄 삭제
        if (!participantsToDelete.isEmpty()) {
            classParticipantRepository.deleteAll(participantsToDelete);
            log.info("학생 일괄 배정 해제 완료 - classId: {}, 성공: {}명, 실패: {}명",
                    classId, successCount, failureCount);
        }

        return AdminClassResponse.StudentOperationResponse.builder()
                .totalCount(request.getStudentIds().size())
                .successCount(successCount)
                .failureCount(failureCount)
                .results(results)
                .build();
    }

    @Override
    public AdminPageResponse<AdminClassResponse.ClassStudentsResponse> getClassStudentsList(
            Long classId,
            AdminClassRequest.SearchFilter filter,
            int page,
            int pageSize) {

        // 클래스 존재 확인
        ClassRoom classRoom = classroomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        // 정렬 조건 생성
        Sort.Direction direction = filter.getSortDirection().equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        // createdAt으로 정렬 (등록일시 기준)
        Sort sort = Sort.by(direction, "createdAt");
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        // 학생 목록 조회 (페이징)
        Page<ClassParticipant> participantPage = classParticipantRepository
                .findByClassIdWithKeyword(classId, filter.getKeyword(), pageable);

        // DTO 변환
        Page<AdminClassResponse.ClassStudentsResponse> responsePage = participantPage
                .map(AdminClassResponse.ClassStudentsResponse::from);

        // AdminPageResponse로 변환하여 반환
        return AdminPageResponse.from(responsePage);
    }

    @Override
    public AdminPageResponse<AdminClassResponse.ClassItem> getClassList(
            AdminClassRequest.SearchFilter filter,
            int page,
            int size) {

        // 정렬 조건 생성
        Sort.Direction direction = filter.getSortDirection().equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, filter.getSortBy());
        Pageable pageable = PageRequest.of(page, size, sort);

        // 페이지 조회
        Page<ClassRoom> classPage = classroomRepository.findByKeywordAndStatusAndDateRange(
                filter.getKeyword(),
                filter.getStartDate(),
                filter.getEndDate(),
                pageable
        );

        // ClassItem으로 변환
        Page<AdminClassResponse.ClassItem> itemPage = classPage.map(classRoom -> {
            Integer studentCount = classParticipantRepository
                    .findByClassRoom_ClassId(classRoom.getClassId())
                    .size();

            String thumbnail = null; // TODO: 썸네일 로직 구현
            String instructorName = classRoom.getUser() != null
                    ? classRoom.getUser().getName()
                    : null;

            return AdminClassResponse.ClassItem.from(
                    classRoom,
                    thumbnail,
                    instructorName,
                    studentCount
            );
        });

        return AdminPageResponse.from(itemPage);
    }
}