package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.ClassAssignmentRequest;
import com.sooscode.sooscode_api.application.classroom.dto.ClassAssignmentResponse;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassAssignment;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassAssignmentRepository;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClassAssignmentServiceImpl implements ClassAssignmentService {
    private final ClassAssignmentRepository classAssignmentRepository;
    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;

    @Override
    public void addClassAssignment(ClassAssignmentRequest rq) {

        log.info("addClassAssignment Service 실행");

        if (classAssignmentRepository.existsByClassRoom_ClassId(rq.getClassId())) {
            throw new CustomException(ErrorCode.ASSIGNMENT_ALREADY_EXISTS);
        }

        User user = userRepository.findById(rq.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ClassRoom classRoom = classRoomRepository.findById(rq.getClassId())
                .orElseThrow(() -> new CustomException(ErrorCode.CLASS_NOT_FOUND));

        ClassAssignment assignment = ClassAssignment.builder()
                .user(user)
                .classRoom(classRoom)
                .build();

        classAssignmentRepository.save(assignment);
    }

    @Override
    public ClassAssignmentResponse getClassAssignment(Long classId) {
        log.info("getClassAssignment Service");

        ClassAssignment assignment = classAssignmentRepository.findByClassRoom_ClassId(classId)
                .orElseThrow(() -> new CustomException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        return ClassAssignmentResponse.from(assignment);
    }

    @Override
    public void deleteClassAssignment(Long classId) {
        log.info("deleteClassAssignment Service");

        ClassAssignment assignment = classAssignmentRepository.findByClassRoom_ClassId(classId)
                .orElseThrow(() -> new CustomException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        classAssignmentRepository.delete(assignment);
    }
}
