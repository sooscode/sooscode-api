package com.sooscode.sooscode_api.domain.classroom.repository;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.enums.ClassMode;
import com.sooscode.sooscode_api.domain.classroom.enums.ClassStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {
    // classId를 통해서 단건 조회
    Optional<ClassRoom> findByClassId(Long classId);
    // Status 상태에 따라 객체 조회
    List<ClassRoom> findByStatus(ClassStatus status);
    // mode 상태에 따라 객체 조회
    List<ClassRoom> findByMode(ClassMode mode);
    // 시작 기간에 따라 객체 조회
    List<ClassRoom> findByStartedAtBetween(LocalDateTime start, LocalDateTime end);
    // userId를 통해서 class의 강사 조회
    List<ClassRoom> findByUser_UserId(Long userId);
}