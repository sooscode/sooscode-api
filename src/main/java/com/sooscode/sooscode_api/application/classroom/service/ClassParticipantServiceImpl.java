package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.ClassParticipantResponse;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassParticipantRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClassParticipantServiceImpl implements ClassParticipantService{
    private final ClassParticipantRepository classParticipantRepository;
    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;

    @Override
    public List<ClassParticipantResponse> getClassStudents(Long classId) {

        List<ClassParticipant> participants =
                classParticipantRepository.findByClassRoom_ClassId(classId);

        if (participants.isEmpty()) {
            throw new CustomException(ErrorCode.CLASS_NOT_FOUND);
        }

        return participants.stream()
                .map(ClassParticipantResponse::from)
                .toList();
    }

    @Override
    public void addParticipant(Long classId, Long userId) {

        log.info("Adding participant to class {} and user {}", classId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLASS_NOT_FOUND));

        ClassParticipant classParticipant = ClassParticipant.builder()
                .user(user)
                .classRoom(classRoom)
                .build();
        classParticipantRepository.save(classParticipant);

    }

    @Override
    public void deleteParticipant(Long classId, Long userId) {
        log.info("Deleting participant from class {} and user {}", classId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLASS_NOT_FOUND));

        // 3. 참가자 조회 (classId + userId)
        ClassParticipant participant = classParticipantRepository
                .findByClassRoom_ClassIdAndUser_UserId(classRoom.getClassId(), user.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.PARTICIPANT_NOT_FOUND));

        classParticipantRepository.delete(participant);
        log.info("Deleted participant from class {} and user {}", classRoom.getClassId(), user.getUserId());
    }
}
