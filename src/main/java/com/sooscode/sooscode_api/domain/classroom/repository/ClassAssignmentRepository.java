package com.sooscode.sooscode_api.domain.classroom.repository;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassAssignmentRepository extends JpaRepository<ClassAssignment, Long> {
//    // ClassId를 통해서 ClassAssignment 객체 조회
//    Optional<ClassAssignment> findByClassId(Long classId);
//    // UserId를 통해서 ClassAssignment 객체 조회
//    Optional<ClassAssignment> findByUserId(Long userId);

}
