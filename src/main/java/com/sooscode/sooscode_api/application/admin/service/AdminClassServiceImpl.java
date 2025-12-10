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

@Service
@RequiredArgsConstructor
public class AdminClassServiceImpl implements AdminClassService {

    private final ClassRoomRepository classroomRepository;
    private final UserRepository userRepository;

    @Override
    public AdminClassResponse.ClassItem createClass(AdminClassRequest.Create request) {

        User instructor = null;
        if(request.getInstructorId() != null){
            instructor = userRepository.findById(request.getInstructorId())
                    .orElseThrow(() -> new CustomException(AdminStatus.USER_NOT_FOUND));
            if(instructor.getRole().equals(UserRole.INSTRUCTOR)){
                new CustomException(AdminStatus.CLASS_INSTRUCTOR_INVALID);
            }
        }

        ClassRoom classRoom = ClassRoom.builder()
                .isOnline(request.getIsOnline())
                .isActive(true)
                .user(instructor)
                .title(request.getTitle())
                .description(request.getDescription())
                .file(null)
                .status(ClassStatus.UPCOMING)
                .mode(ClassMode.FREE)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
        classroomRepository.save(classRoom);

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
    public AdminClassResponse.ClassItem getClassDetail(Long classId) {
        return null;
    }

    @Override
    public void assignInstructor(Long classId, AdminClassRequest.AssignInstructor request) {

    }

    @Override
    public void assignStudents(Long classId, AdminClassRequest.AssignStudents request) {

    }
}
