package com.sooscode.sooscode_api.domain.classroom.repository;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassAssignment;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

public interface ClassAssignmentRepository extends JpaRepository<ClassAssignment, Long> {

    Optional<ClassAssignment> findByClassRoom_ClassId(Long id);
    boolean existsByClassRoom_ClassId(Long classId);
    List<ClassAssignment> findByUser_UserId(Long userId);

    /**
     * 내가 참여하고 있는 클래스 조회
     */
    List<ClassAssignment> findByClassRoom(ClassRoom classRoom);
}