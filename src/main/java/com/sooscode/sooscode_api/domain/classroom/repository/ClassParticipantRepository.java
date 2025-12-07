package com.sooscode.sooscode_api.domain.classroom.repository;

import com.sooscode.sooscode_api.application.classroom.dto.classroom.MyClassResponse;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import com.sooscode.sooscode_api.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassParticipantRepository extends JpaRepository<ClassParticipant, Long> {
    // classId를 전달받아서 Participant를 List로 반환하는 Repo
    List<ClassParticipant> findByClassRoom_ClassId(Long classId);
    // classId와 userId를 조합해서 Participant를 찾아서 반환
    Optional<ClassParticipant> findByClassRoom_ClassIdAndUser_UserId(Long classId, Long userId);
    @Query("""
        SELECT new com.sooscode.sooscode_api.application.classroom.dto.classroom.MyClassResponse(
            cr.classId,
            cr.title,
            f.url,
            t.name
        )
        FROM ClassParticipant cp
        JOIN cp.classRoom cr
        LEFT JOIN cr.file f
        LEFT JOIN ClassAssignment ca ON ca.classRoom = cr
        LEFT JOIN ca.user t
        WHERE cp.user.userId = :userId
        """)
    Page<MyClassResponse> findMyClasses(@Param("userId") Long userId, Pageable pageable);

    /**
     * 특정 클래스에 참여하고 있는 유저 조회
     */
    List<ClassParticipant> findByUser(User user);

}
