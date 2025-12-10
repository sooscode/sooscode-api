package com.sooscode.sooscode_api.application.mypage.service;

import com.sooscode.sooscode_api.application.mypage.dto.MypageClassDetailResponse;
import com.sooscode.sooscode_api.application.mypage.dto.MypageMyclassesResponse;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassParticipantRepository;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import com.sooscode.sooscode_api.domain.file.repository.SooFileRepository;
import com.sooscode.sooscode_api.domain.snapshot.repository.CodeSnapshotRepository;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.ClassStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageClassServiceImpl implements MypageClassService {
    private final ClassRoomRepository classRoomRepository;
    private final ClassParticipantRepository classParticipantRepository;

    /**
     * Class의 정보를 조회
     */
    @Override
    public MypageClassDetailResponse getClassDetail(Long classId) {
        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassStatus.CLASS_NOT_FOUND));

        return MypageClassDetailResponse.from(classRoom);
    }

    // 학생이 가지고있는 class의 list를 조회
    @Override
    public List<MypageMyclassesResponse> getStudentClasses(Long userId) {
        log.info("getStudentClasses Service");

        List<ClassParticipant> classes = classParticipantRepository.findByUser_UserId(userId);

        log.info(classes.toString());

        return classes.stream()
                .map(cp -> MypageMyclassesResponse.from(cp.getClassRoom()))
                .toList();
    }


    // 강사가 가지고있는 class의 list를 조회
    @Override
    public List<MypageMyclassesResponse> getTeacherClasses(Long userId) {
        log.info("getTeacherClasses Service");

        List<ClassRoom> classes = classRoomRepository.findByUser_UserId(userId);

        return classes.stream()
                .map(MypageMyclassesResponse::from)
                .toList();
    }


}
