package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.*;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassAssignment;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.enums.ClassMode;
import com.sooscode.sooscode_api.domain.classroom.enums.ClassStatus;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassAssignmentRepository;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassParticipantRepository;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import com.sooscode.sooscode_api.domain.file.entity.SooFile;
import com.sooscode.sooscode_api.domain.file.repository.SooFileRepository;
import com.sooscode.sooscode_api.domain.snapshot.entity.CodeSnapshot;
import com.sooscode.sooscode_api.domain.snapshot.repository.CodeSnapshotRepository;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new CustomException(ErrorCode.CLASS_NOT_FOUND));

        return ClassRoomResponse.Detail.from(classRoom);
    }

    @Override
    public ClassRoom createClassRoom(ClassRoomCreateRequest request) {

        SooFile file = null;
        if (request.getFileId() != null) {
            file = sooFileRepository.findById(request.getFileId())
                    .orElse(null);
        }

        ClassRoom classRoom = ClassRoom.builder()
                .isOnline(request.isOnline())
                .title(request.getTitle())
                .description(request.getDescription())
                .file(file)
                .status(request.getStatus())
                .status(ClassStatus.UPCOMING)
                .mode(ClassMode.QUIZ)
                .startedAt(request.getStartedAt())
                .endedAt(request.getEndedAt())
                .build();

        return classRoomRepository.save(classRoom);
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

//    @Override
//    public ClassDetailResponse getTeacherClassDetail(Long classId, LocalDate date) {
//        if(date == null){
//            date = LocalDate.now();
//        }
//
//        ClassRoom classRoom = classRoomRepository
//                .findByClassId(classId)
//                .orElseThrow(() -> new CustomException(ErrorCode.CLASS_NOT_FOUND));
//
//        ClassInfoResponse info = ClassInfoResponse.from(classRoom);
//
//        List<ClassParticipantResponse> participants =
//                classParticipantRepository.findByClassRoom_ClassId(classId)
//                        .stream()
//                        .map(ClassParticipantResponse::from)
//                        .toList();
//
//        List<SnapshotResponse> snapshots =
//                codeSnapshotRepository.findByClassIdAndCreatedAtBetween(
//                        classId,
//                        date.atStartOfDay(),
//                        date.atTime(LocalTime.MAX)
//                ).stream().map(SnapshotResponse::from)
//                        .toList();
//
//        List<FileResponse> fileList =
//                sooFileRepository.findByClassIdAndCreatedAtBetween(
//                        classId,
//                        date.atStartOfDay(),
//                        date.atTime(LocalTime.MAX)
//                ).stream().map(FileResponse::from).toList();
//        return ClassDetailResponse.builder()
//                .info(info)
//                .participants(participants)
//                .snapshots(snapshots)
//                .fileList(fileList)
//                .build();
//    }


    /**
     * 모든 클래스 조회
     */
//    public List<ClassRoomResponse> getAllClasses() {
//        return classRoomRepository.findAll().stream()
//                .map(ClassRoomResponse::from)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * 클래스 상세 조회
//     */
//    public ClassRoomResponse getClassById(Long classId) {
//        ClassRoom classRoom = classRoomRepository.findById(classId)
//                .orElseThrow(() -> new CustomException(ErrorCode.CLASS_NOT_FOUND));
//
//        return ClassRoomResponse.from(classRoom);
//    }
//
//    /**
//     * 클래스 입장 (접속 가능 여부 확인)
//     * Service에서 검증하고 예외 던짐
//     */
//    @Transactional
//    public void enterClass(Long classId, Long userId) {
//        // 1. 클래스 조회
//        ClassRoom classRoom = classRoomRepository.findById(classId)
//                .orElseThrow(() -> new CustomException(ErrorCode.CLASS_NOT_FOUND));
//
//        // 2. 오프라인 클래스 체크
//        if (!classRoom.isOnlineClass()) {
//            throw new CustomException(ErrorCode.CLASS_OFFLINE);
//        }
//
//        // 3. 시작 전 체크
//        if (!classRoom.isStarted()) {
//            throw new CustomException(ErrorCode.CLASS_NOT_STARTED);
//        }
//
//        // 4. 종료 체크
//        if (classRoom.isEnded()) {
//            throw new CustomException(ErrorCode.CLASS_ALREADY_ENDED);
//        }
//
//        log.info("[ClassRoom] 클래스 입장 - 클래스ID: {}, 사용자ID: {}", classId, userId);
//
//        // TODO: ClassParticipant 엔티티에 참가 기록 저장
//    }

}
