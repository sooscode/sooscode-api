package com.sooscode.sooscode_api.application.mypage.service;

import com.sooscode.sooscode_api.application.mypage.dto.MypageClassDetailResponse;
import com.sooscode.sooscode_api.application.mypage.dto.MypageMyclassesResponse;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassParticipantRepository;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.ClassRoomStatus;
import com.sooscode.sooscode_api.global.api.status.UserStatus;
import com.sooscode.sooscode_api.infra.file.service.S3FileService;
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
public class MypageClassServiceImpl implements MypageClassService {
    private final ClassRoomRepository classRoomRepository;
    private final ClassParticipantRepository classParticipantRepository;
    private final UserRepository userRepository;
    private final S3FileService s3FileService;

    /**
     * Class의 정보를 조회
     */
    @Override
    public MypageClassDetailResponse getClassDetail(Long classId) {

        // 클래스가 존재하는지 검증
        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        String thumbnailUrl = null;

        if (classRoom.getFile() != null) {
            thumbnailUrl = s3FileService.getPublicUrl(
                    classRoom.getFile().getFileId()
            );
        }

        return MypageClassDetailResponse.from(classRoom,thumbnailUrl);
    }

//    // 학생이 가지고있는 class의 list를 조회
//    @Override
//    public List<MypageMyclassesResponse> getStudentClasses(Long userId) {
//        log.info("getStudentClasses Service");
//
//        // 결과 0건 조회시 빈 리스트 반환
//        List<ClassParticipant> classes = classParticipantRepository.findByUser_UserId(userId);
//
//        log.info(classes.toString());
//
//        return classes.stream()
//                .map(cp -> MypageMyclassesResponse.from(cp.getClassRoom()))
//                .toList();
//    }
//
//
//    // 강사가 가지고있는 class의 list를 조회
//    @Override
//    public List<MypageMyclassesResponse> getTeacherClasses(Long userId) {
//        log.info("getTeacherClasses Service");
//
//        // user가 존재하는지 검증
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new CustomException(UserStatus.NOT_FOUND));
//
//        // 결과 0건 조회시 빈 리스트 반환
//        List<ClassRoom> classes = classRoomRepository.findByUser_UserId(userId);
//
//        return classes.stream()
//                .map(MypageMyclassesResponse::from)
//                .toList();
//    }
    @Override
    public Page<MypageMyclassesResponse> getStudentClasses(Long userId, Pageable pageable) {
        log.info("getStudentClasses Service");

        Page<ClassParticipant> pageResult =
                classParticipantRepository.findByUser_UserId(userId, pageable);

        return pageResult.map(cp ->
                MypageMyclassesResponse.from(cp.getClassRoom())
        );
    }

    @Override
    public Page<MypageMyclassesResponse> getTeacherClasses(Long userId, Pageable pageable) {
        log.info("getTeacherClasses Service");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserStatus.NOT_FOUND));

        Page<ClassRoom> pageResult =
                classRoomRepository.findByUser_UserId(userId, pageable);

        return pageResult.map(MypageMyclassesResponse::from);
    }


}
