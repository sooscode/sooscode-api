package com.sooscode.sooscode_api.application.mypage.service;

import com.sooscode.sooscode_api.application.mypage.dto.MypageClassFileDeleteRequest;
import com.sooscode.sooscode_api.application.mypage.dto.MypageClassFileUploadRequest;
import com.sooscode.sooscode_api.application.mypage.dto.MypageClassFileResponse;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoomFile;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomFileRepository;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;

import com.sooscode.sooscode_api.domain.file.entity.SooFile;
import com.sooscode.sooscode_api.domain.file.enums.FileType;
import com.sooscode.sooscode_api.domain.file.repository.SooFileRepository;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.infra.s3.service.S3FileService;

import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;

import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.status.ClassRoomStatus;
import com.sooscode.sooscode_api.global.status.FileStatus;
import com.sooscode.sooscode_api.global.status.UserStatus;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MypageClassFileServiceImpl implements MypageClassFileService {

    private final ClassRoomRepository classRoomRepository;
    private final UserRepository userRepository;
    private final ClassRoomFileRepository classRoomFileRepository;
    private final S3FileService s3FileService;
    private final EntityManager em;
    private final SooFileRepository sooFileRepository;

    /**
     * 1) 클래스 자료 업로드 (DTO 기반)
     */
    @Override
    public List<MypageClassFileResponse> uploadFiles(MypageClassFileUploadRequest request) throws Exception {

        Long classId = request.getClassId();
        Long teacherId = request.getTeacherId();
        LocalDate date = LocalDate.parse(request.getLectureDate());

        log.info("uploadFiles Service | classId={}, teacherId={}, date={}, fileCount={}",
                classId, teacherId, request.getFiles().size());

        // 파일이 깨져서 올 수 있으니 2차적으로 파일이 존재하는지 검증
        if (request.getFiles() == null || request.getFiles().isEmpty()) {
            throw new CustomException(FileStatus.REQUIRED);
        }

        // 클래스가 존재하는지 검증
        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        // 유저가 존재하는지 검증
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new CustomException(UserStatus.NOT_FOUND));

        UserRole userRole = teacher.getRole();
        if (userRole == UserRole.STUDENT) {
            throw new CustomException(FileStatus.INVALID_ROLE);
        }


        List<MypageClassFileResponse> result = new ArrayList<>();

        for (var file : request.getFiles()) {

            SooFile savedFile = s3FileService.uploadFile(file, FileType.LECTURE_MATERIAL);

            ClassRoomFile classRoomFile = classRoomFileRepository.save(
                    ClassRoomFile.builder()
                            .classRoom(classRoom)
                            .file(savedFile)
                            .uploadedBy(teacher)
                            .lectureDate(date)
                            .build()
            );

            result.add(MypageClassFileResponse.from(classRoomFile));
        }

        return result;
    }


    /**
     * 클래스 전체 자료 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MypageClassFileResponse> getFilesByClassId(Long classId, Pageable pageable) {

        // classRoomFile이 존재하는지 검증
        // 존재하지 않으면 빈 리스트를 반환
        Page<ClassRoomFile> page =
                classRoomFileRepository.findByClassRoom_ClassId(classId, pageable);

        return page.map(MypageClassFileResponse::from);
    }


    /**
     * 특정 날짜 자료 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MypageClassFileResponse> getFilesByLectureDate(
            Long classId,
            LocalDate lectureDate,
            Pageable pageable
    ) {
        // classRoomFile이 존재하는지 검증
        // 존재하지 않으면 빈 리스트를 반환
        Page<ClassRoomFile> page =
                classRoomFileRepository.findByClassRoom_ClassIdAndLectureDate(classId, lectureDate, pageable);

        return page.map(MypageClassFileResponse::from);
    }

    @Override
    public void deleteFiles(MypageClassFileDeleteRequest rq) throws Exception {

        Long teacherId = rq.getTeacherId();

        for (Long classRoomFileId : rq.getFileIds()) {

            ClassRoomFile crf = classRoomFileRepository.findById(classRoomFileId)
                    .orElseThrow(() -> new CustomException(FileStatus.NOT_FOUND));

            // 업로더 확인
            if (!crf.getUploadedBy().getUserId().equals(teacherId)) {
                throw new CustomException(FileStatus.FORBIDDEN_ACCESS);
            }

            // 1) 필요한 애들 먼저 꺼내놓기 (Lazy 방지)
            SooFile sooFile = crf.getFile();

            // 2) ClassRoomFile 삭제
            classRoomFileRepository.delete(crf);

            // 3) Hibernate flush → 여기서 UPDATE 꼬임 제거
            em.flush();

            // 4) S3 삭제
            s3FileService.deleteFile(sooFile);

            // 5) SooFile 엔티티 삭제
            sooFileRepository.delete(sooFile);

            log.info("deleteFiles | classRoomFileId={} 삭제 완료", classRoomFileId);
        }
    }



}
