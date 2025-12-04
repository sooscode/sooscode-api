package com.sooscode.sooscode_api.domain.classroom.repository;

import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassParticipantRepository extends JpaRepository<ClassParticipant, Long> {
    // classId를 전달받아서 Participant를 List로 반환하는 Repo
    List<ClassParticipant> findByClassRoom_ClassId(Long classId);
    // classId와 userId를 조합해서 Participant를 찾아서 반환
    Optional<ClassParticipant> findByClassRoom_ClassIdAndUser_UserId(Long classId, Long userId);

}
