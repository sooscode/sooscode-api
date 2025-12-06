package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.participant.ClassParticipantResponse;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassParticipantRepository;
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
            throw new CustomException(ClassErrorCode.PARTICIPANT_NOT_FOUND);
        }

        return participants.stream()
                .map(ClassParticipantResponse::from)
                .toList();
    }
}
