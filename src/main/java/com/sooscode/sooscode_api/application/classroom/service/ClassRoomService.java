package com.sooscode.sooscode_api.application.classroom.service;

//import com.sooscode.sooscode_api.application.classroom.dto.ClassDetailResponse;
import com.sooscode.sooscode_api.application.classroom.dto.classroom.ClassRoomCreateRequest;
import com.sooscode.sooscode_api.application.classroom.dto.classroom.ClassRoomResponse;
import com.sooscode.sooscode_api.application.classroom.dto.classroom.MyClassResponse;
import com.sooscode.sooscode_api.application.classroom.dto.classroom.TeacherClassResponse;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClassRoomService {

    /** 클래스 상세 정보 조회 */
    ClassRoomResponse.Detail getClassDetail(Long classId);
    /** 특정 선생이 맡은 클래스 목록 조회 */
    List<TeacherClassResponse> getClassesByTeacher(Long userId);
    /** 유저가 참여하거나 담당 중인 클래스 목록 조회 */
    Page<MyClassResponse> getMyClasses(Long userId, Pageable pageable);
}
