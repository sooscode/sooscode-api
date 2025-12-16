package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.ClassRoomDetailResponse;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassParticipantRepository;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import com.sooscode.sooscode_api.domain.file.entity.SooFile;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.ClassRoomStatus;
import com.sooscode.sooscode_api.infra.file.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassRoomServiceImpl implements ClassRoomService {

    private final ClassRoomRepository classRoomRepository;
    private final ClassParticipantRepository classParticipantRepository;
    private final S3FileService s3FileService;

    @Override
    public ClassRoomDetailResponse getClassRoomDetail(Long classId, Long userId) {

        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        if (!classRoom.isActive()) {
            throw new CustomException(ClassRoomStatus.CLASS_NOT_ACTIVE);
        }

        validateClassJoinTime(classRoom);

        boolean isInstructor = classRoom.getUser().getUserId().equals(userId);
        boolean isParticipant = classParticipantRepository
                .findByClassRoom_ClassIdAndUser_UserId(classId, userId)
                .isPresent();

        if (!isInstructor && !isParticipant) {
            throw new CustomException(ClassRoomStatus.CLASS_ACCESS_DENIED);
        }

        int participantCount = classParticipantRepository
                .countByClassRoom_ClassId(classId);

        int totalParticipantCount = participantCount + 1; // 강사 포함

        return ClassRoomDetailResponse.from(classRoom, totalParticipantCount, isInstructor);
    }

    private void validateClassJoinTime(ClassRoom classRoom) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime classStart = LocalDateTime.of(classRoom.getStartDate(), classRoom.getStartTime());
        LocalDateTime classEnd = LocalDateTime.of(classRoom.getEndDate(), classRoom.getEndTime());

        if (now.isBefore(classStart)) {
            throw new CustomException(ClassRoomStatus.CLASS_NOT_STARTED);
        }

        if (now.isAfter(classEnd)) {
            throw new CustomException(ClassRoomStatus.CLASS_ALREADY_ENDED);
        }
    }
    // 효상 - 썸네일 업로드 TEST
    @Transactional
    @Override
    public void updateThumbnail(Long classId, Long userId, MultipartFile thumbnail) throws IOException {

        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_ALREADY_ENDED));

        // 소유자 검증
        if (!classRoom.getUser().getUserId().equals(userId)) {
            throw new CustomException(ClassRoomStatus.CLASS_ALREADY_ENDED);
        }

        // 기존 썸네일
        SooFile oldFile = classRoom.getFile();

        if (thumbnail != null && !thumbnail.isEmpty()) {

            if (oldFile != null) {
                s3FileService.deleteFile(oldFile);
            }

            SooFile newFile = s3FileService.uploadThumbnail(thumbnail);
            classRoom.setFile(newFile);
        }
    }

}