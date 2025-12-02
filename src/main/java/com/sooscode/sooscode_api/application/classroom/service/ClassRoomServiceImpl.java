package com.sooscode.sooscode_api.application.classroom.service;

import com.sooscode.sooscode_api.application.classroom.dto.*;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassRoomServiceImpl implements ClassRoomService {
    private final ClassRoomRepository classRoomRepository;

    /**
     * 모든 클래스 조회
     */
    public List<ClassRoomResponse> getAllClasses() {
        return classRoomRepository.findAll().stream()
                .map(ClassRoomResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 클래스 상세 조회
     */
    public ClassRoomResponse getClassById(Long classId) {
        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLASS_NOT_FOUND));

        return ClassRoomResponse.from(classRoom);
    }

    /**
     * 클래스 입장 (접속 가능 여부 확인)
     * ✅ Service에서 검증하고 예외 던짐
     */
    @Transactional
    public void enterClass(Long classId, Long userId) {
        // 1. 클래스 조회
        ClassRoom classRoom = classRoomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLASS_NOT_FOUND));

        // 2. 오프라인 클래스 체크
        if (!classRoom.isOnlineClass()) {
            throw new CustomException(ErrorCode.CLASS_OFFLINE);
        }

        // 3. 시작 전 체크
        if (!classRoom.isStarted()) {
            throw new CustomException(ErrorCode.CLASS_NOT_STARTED);
        }

        // 4. 종료 체크
        if (classRoom.isEnded()) {
            throw new CustomException(ErrorCode.CLASS_ALREADY_ENDED);
        }

        log.info("[ClassRoom] 클래스 입장 - 클래스ID: {}, 사용자ID: {}", classId, userId);

        // TODO: ClassParticipant 엔티티에 참가 기록 저장
    }

}
