package com.sooscode.sooscode_api.application.admin.service;

import com.sooscode.sooscode_api.application.admin.dto.AdminClassRequest;
import com.sooscode.sooscode_api.application.admin.dto.AdminClassResponse;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.enums.ClassMode;
import com.sooscode.sooscode_api.domain.classroom.enums.ClassStatus;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.AdminStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminClassServiceImpl implements AdminClassService {

    private final ClassRoomRepository classroomRepository;
    private final UserRepository userRepository;

    @Override
    public AdminClassResponse.ClassItem createClass(AdminClassRequest.Create request) {
        // ===== 1. 강사 조회 =====
        User instructor = null;
        if(request.getInstructorId() == null){
            instructor = userRepository.findById(request.getInstructorId())
                    .orElseThrow(() -> new CustomException(AdminStatus.USER_NOT_FOUND));
            if(instructor.getRole().equals(UserRole.INSTRUCTOR)){
                new CustomException(AdminStatus.CLASS_INSTRUCTOR_INVALID);
            }
        }

        // ===== 3. 클래스 엔티티 생성 =====
        ClassRoom classRoom = ClassRoom.builder()
                .isOnline(request.getIsOnline())
                .isActive(true)
                .user(instructor)
                .title(request.getTitle())
                .description(request.getDescription())
                .file(null)
                .status(ClassStatus.UPCOMING)
                .mode(ClassMode.FREE)
                .startedAt(request.getStartedAt())
                .endedAt(request.getEndedAt())
                .build();
        classroomRepository.save(classRoom);

        // ===== 4. 초기 학생 수 = 0 =====
        Integer studentCount = 0;
        String thumbnail = null;

        return AdminClassResponse.ClassItem.from(
                classRoom,
                thumbnail,
                instructor.getName(),
                studentCount
        );
    }

    @Override
    @Transactional
    public AdminClassResponse.Detail updateClass(Long classId, AdminClassRequest.Update request) {
        ClassRoom classRoom = classroomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(com.sooscode.sooscode_api.global.api.status.ClassStatus.CLASS_NOT_FOUND));

        User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new CustomException(AdminStatus.USER_NOT_FOUND));
        if(instructor.getRole().equals(UserRole.INSTRUCTOR)){
            new CustomException(AdminStatus.CLASS_INSTRUCTOR_INVALID);
        }

        classRoom.setTitle(request.getTitle());
        classRoom.setDescription(request.getDescription());

        return null;
    }

    @Override
    public void deleteClass(Long classId) {

    }

    @Override
    public AdminClassResponse.ClassListPage getClassList(AdminClassRequest.SearchFilter filter, int page, int size) {
        return null;
    }

    @Override
    public AdminClassResponse.Detail getClassDetail(Long classId) {
        return null;
    }

    @Override
    public void assignInstructor(Long classId, AdminClassRequest.AssignInstructor request) {

    }

    @Override
    public void assignStudents(Long classId, AdminClassRequest.AssignStudents request) {

    }
}
