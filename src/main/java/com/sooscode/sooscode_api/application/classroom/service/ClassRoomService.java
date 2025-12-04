package com.sooscode.sooscode_api.application.classroom.service;

//import com.sooscode.sooscode_api.application.classroom.dto.ClassDetailResponse;
import com.sooscode.sooscode_api.application.classroom.dto.ClassParticipantResponse;
import com.sooscode.sooscode_api.application.classroom.dto.ClassRoomCreateRequest;
import com.sooscode.sooscode_api.application.classroom.dto.ClassRoomResponse;
import com.sooscode.sooscode_api.application.classroom.dto.TeacherClassResponse;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClassRoomService {
    // Class 정보
    ClassRoomResponse.Detail getClassDetail(Long classId);
    // Test용 classroom Create
    ClassRoom createClassRoom(ClassRoomCreateRequest request);

    List<TeacherClassResponse> getClassesByTeacher(Long userId);
    // 강사 ClassRoom 진입시 getData
    //ClassDetailResponse getTeacherClassDetail(Long classId);


    // 특정 유저가 참여한 클래스 목록
    //List<ClassRoomResponse.Summary> getUserClasses(Long userId);

    // 특정 선생이 만든 클래스 목록
    //List<ClassRoomResponse.Summary> getTeacherClasses(Long teacherId);

    // 특정 학생이 참여 중인 클래스 목록
    //List<ClassRoomResponse.Summary> getStudentClasses(Long studentId);

    // 클래스 존재 여부 검증 (없으면 예외)
    //ClassRoom validateClass(Long classId);

    // 특정 유저가 해당 클래스에 참여 중인지 확인
    //boolean isUserInClass(Long userId, Long classId);



    /* ===== 참여자 관리 ===== */
    //void joinClass(Long userId, Long classId);
    //List<ClassParticipantResponse> getClassParticipants(Long classId);

    /* ===== 대시보드 / 스냅샷 ===== */
    //ClassRoomResponse.Stats getClassStats(Long classId);
    //ClassRoomResponse.RecentSnapshots getRecentSnapshots(Long classId);

    /* ===== 클래스 수정 ===== */
    //ClassRoom updateClassRoom(Long classId, ClassRoomUpdateRequest request);

}
