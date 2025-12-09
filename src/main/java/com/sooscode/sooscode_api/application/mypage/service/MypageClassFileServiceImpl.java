package com.sooscode.sooscode_api.application.mypage.service;

import com.sooscode.sooscode_api.application.mypage.dto.MypageClassFileDeleteRequest;
import com.sooscode.sooscode_api.application.mypage.dto.MypageClassFileUploadRequest;
import com.sooscode.sooscode_api.application.mypage.dto.ClassRoomFileResponse;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoomFile;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomFileRepository;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;

import com.sooscode.sooscode_api.domain.file.entity.SooFile;
import com.sooscode.sooscode_api.domain.file.enums.FileType;
import com.sooscode.sooscode_api.domain.file.repository.SooFileRepository;
import com.sooscode.sooscode_api.infra.file.service.S3FileService;

import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;

import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.errorcode.ClassErrorCode;
import com.sooscode.sooscode_api.global.exception.errorcode.FileErrorCode;
import com.sooscode.sooscode_api.global.exception.errorcode.UserErrorCode;

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
    public List<ClassRoomFileResponse> uploadFiles(MypageClassFileUploadRequest rq) throws Exception {

        Long classId = rq.getClassId();
        Long teacherId = rq.getTeacherId();
        LocalDate date = LocalDate.parse(rq.getLectureDate());

        log.info("uploadFiles Service | classId={}, teacherId={}, date={}, fileCount={}",
                classId, teacherId, rq.getFiles().size());

        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassErrorCode.CLASS_NOT_FOUND));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));

        List<ClassRoomFileResponse> result = new ArrayList<>();

        for (var file : rq.getFiles()) {

            SooFile savedFile = s3FileService.uploadFile(file, FileType.LECTURE_MATERIAL);

            ClassRoomFile classRoomFile = classRoomFileRepository.save(
                    ClassRoomFile.builder()
                            .classRoom(classRoom)
                            .file(savedFile)
                            .uploadedBy(teacher)
                            .lectureDate(date)
                            .build()
            );

            result.add(ClassRoomFileResponse.from(classRoomFile));
        }

        return result;
    }


    /**
     * 클래스 전체 자료 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ClassRoomFileResponse> getFilesByClassId(Long classId, Pageable pageable) {

        Page<ClassRoomFile> page =
                classRoomFileRepository.findByClassRoom_ClassId(classId, pageable);

        return page.map(ClassRoomFileResponse::from);
    }


    /**
     * 특정 날짜 자료 조회
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ClassRoomFileResponse> getFilesByLectureDate(
            Long classId,
            LocalDate lectureDate,
            Pageable pageable
    ) {
        Page<ClassRoomFile> page =
                classRoomFileRepository.findByClassRoom_ClassIdAndLectureDate(classId, lectureDate, pageable);

        return page.map(ClassRoomFileResponse::from);
    }

    @Override
    public void deleteFiles(MypageClassFileDeleteRequest rq) throws Exception {

        Long teacherId = rq.getTeacherId();

        for (Long classRoomFileId : rq.getFileIds()) {

            ClassRoomFile crf = classRoomFileRepository.findById(classRoomFileId)
                    .orElseThrow(() -> new CustomException(FileErrorCode.NOT_FOUND));

            // 업로더 확인
            if (!crf.getUploadedBy().getUserId().equals(teacherId)) {
                throw new CustomException(FileErrorCode.FORBIDDEN_ACCESS);
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
