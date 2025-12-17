package com.sooscode.sooscode_api.application.code.dto;

import lombok.Data;

@Data
public class CodeShareDto {

    private Long classId;      // 어떤 강의방인지
    private Long userId;       // 누가 보냈는지 (서버에서 채움)
    private String userName;   // 선택 옵션
    private String language;   // Java, Python 등
    private String code;       // 전송된 코드 내용
    private String output;     // 결과
    private boolean isInstructor; // 강사인지 학생인지
    private String type;          // INSTRUCTOR_EXAMPLE, STUDENT_EDIT, null
    private String userEmail;     // 발신자 이메일
    private String targetEmail;   // 편집 대상 학생 이메일
}