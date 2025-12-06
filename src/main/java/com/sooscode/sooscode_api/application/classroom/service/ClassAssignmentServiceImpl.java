package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.assignment.ClassAssignmentRequest;
import com.sooscode.sooscode_api.application.classroom.dto.assignment.ClassAssignmentResponse;
import com.sooscode.sooscode_api.application.classroom.dto.classroom.TeacherListItemResponse;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassAssignment;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassAssignmentRepository;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.errorcode.ClassErrorCode;
import com.sooscode.sooscode_api.global.exception.errorcode.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClassAssignmentServiceImpl implements ClassAssignmentService {
    private final ClassAssignmentRepository classAssignmentRepository;
    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;

    @Override
    public ClassAssignmentResponse getClassAssignment(Long classId) {
        log.info("getClassAssignment Service");

        ClassAssignment assignment = classAssignmentRepository.findByClassRoom_ClassId(classId)
                .orElseThrow(() -> new CustomException(ClassErrorCode.ASSIGNMENT_NOT_FOUND));

        return ClassAssignmentResponse.from(assignment);
    }

    @Override
    public List<TeacherListItemResponse> getAssignmentTeachers() {

        // 전체 Assignment를 가져와서 assigns에 담음
        List<ClassAssignment> assigns = classAssignmentRepository.findAll();

        Map<Long, User> userMap = new HashMap<>();

        // assign 전체를 순회하면서 assign의 User객체를 뽑아서 중복 제거후 userMap에 put
        for (ClassAssignment a : assigns) {
            User u = a.getUser();
            userMap.putIfAbsent(u.getUserId(), u);
        }

        //  TeacherListItemResponse를 List로 담아서 반환
        return userMap.values().stream()
                .map(TeacherListItemResponse::from)
                .toList();
    }
}
