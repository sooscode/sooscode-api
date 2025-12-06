package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.classroom.ClassRoomCreateRequest;
import com.sooscode.sooscode_api.application.classroom.dto.classroom.ClassRoomResponse;
import com.sooscode.sooscode_api.application.classroom.dto.classroom.MyClassResponse;
import com.sooscode.sooscode_api.application.classroom.dto.classroom.TeacherClassResponse;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassAssignment;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.enums.ClassMode;
import com.sooscode.sooscode_api.domain.classroom.enums.ClassStatus;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassAssignmentRepository;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassParticipantRepository;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import com.sooscode.sooscode_api.domain.file.entity.SooFile;
import com.sooscode.sooscode_api.domain.file.repository.SooFileRepository;
import com.sooscode.sooscode_api.domain.snapshot.repository.CodeSnapshotRepository;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.errorcode.ClassErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassRoomServiceImpl implements ClassRoomService {
    private final ClassRoomRepository classRoomRepository;
    private final SooFileRepository sooFileRepository;
    private final ClassParticipantRepository classParticipantRepository;
    private final CodeSnapshotRepository codeSnapshotRepository;
    private final UserRepository userRepository;
    private final ClassAssignmentRepository classAssignmentRepository;

    /**
     * Class의 정보를 조회
     */
    @Override
    public ClassRoomResponse.Detail getClassDetail(Long classId) {
        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassErrorCode.CLASS_NOT_FOUND));

        return ClassRoomResponse.Detail.from(classRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherClassResponse> getClassesByTeacher(Long userId) {

        log.info("getClassesByTeacher Service");

        List<ClassAssignment> assignments =
                classAssignmentRepository.findByUser_UserId(userId);

        return assignments.stream()
                .map(TeacherClassResponse::from)
                .toList();
    }

    @Override
    public Page<MyClassResponse> getMyClasses(Long userId, Pageable pageable) {

        log.info("getMyClasses Service");

        return classParticipantRepository.findMyClasses(userId, pageable);
    }
}
