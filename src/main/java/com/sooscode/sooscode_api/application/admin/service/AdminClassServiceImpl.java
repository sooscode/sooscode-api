package com.sooscode.sooscode_api.application.admin.service;

import com.sooscode.sooscode_api.application.admin.dto.AdminClassRequest;
import com.sooscode.sooscode_api.application.admin.dto.AdminClassResponse;
import com.sooscode.sooscode_api.application.admin.dto.AdminPageResponse;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.enums.ClassMode;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassParticipantRepository;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.status.AdminStatus;
import com.sooscode.sooscode_api.global.status.ClassRoomStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminClassServiceImpl implements AdminClassService {

    private final ClassRoomRepository classroomRepository;
    private final UserRepository userRepository;
    private final ClassParticipantRepository classParticipantRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public AdminClassResponse.ClassItem createClass(AdminClassRequest.Create request) {
        // 강사 검증
        User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new CustomException(AdminStatus.USER_NOT_FOUND));

        if (!instructor.getRole().equals(UserRole.INSTRUCTOR)) {
            throw new CustomException(AdminStatus.CLASS_INSTRUCTOR_INVALID);
        }

        // 클래스 생성
        ClassRoom classRoom = ClassRoom.builder()
                .isOnline(request.getIsOnline())
                .isActive(true)
                .user(instructor)
                .title(request.getTitle())
                .description(request.getDescription())
                .file(null)
                .mode(ClassMode.FREE)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        classroomRepository.save(classRoom);

        // 응답 생성
        Integer studentCount = 0;
        String thumbnail = null;
        String instructorName = (instructor != null) ? instructor.getName() : null;

        return AdminClassResponse.ClassItem.from(
                classRoom,
                thumbnail,
                instructorName,
                studentCount
        );
    }

    @Override
    @Transactional
    public AdminClassResponse.ClassItem updateClass(Long classId, AdminClassRequest.Update request) {
        // 클래스 조회
        ClassRoom classRoom = classroomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        // 강사 검증 및 변경
        User instructor = null;
        if (request.getInstructorId() != null) {
            instructor = userRepository.findById(request.getInstructorId())
                    .orElseThrow(() -> new CustomException(AdminStatus.USER_NOT_FOUND));

            // 강사 권한 확인
            if (!instructor.getRole().equals(UserRole.INSTRUCTOR)) {
                throw new CustomException(AdminStatus.CLASS_INSTRUCTOR_INVALID);
            }
            classRoom.setUser(instructor);
        }

        classRoom.setTitle(request.getTitle());
        classRoom.setDescription(request.getDescription());
        classRoom.setOnline(request.getIsOnline());
        classRoom.setStartDate(request.getStartDate());
        classRoom.setEndDate(request.getEndDate());
        classRoom.setStartTime(request.getStartTime());
        classRoom.setEndTime(request.getEndTime());

        // 응답 생성
        List<ClassParticipant> participants = classParticipantRepository.findByClassRoom_ClassId(classId);
        Integer studentCount = participants.size();
        String thumbnail = null;
        String instructorName = classRoom.getUser() != null ? classRoom.getUser().getName() : null;

        return AdminClassResponse.ClassItem.from(
                classRoom,
                thumbnail,
                instructorName,
                studentCount
        );
    }

    @Override
    @Transactional
    public void deleteClass(Long classId) {
        // 클래스 조회
        ClassRoom classRoom = classroomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        // Soft Delete: isActive를 false로 변경
        classRoom.setActive(false);
    }

    @Override
    public AdminClassResponse.ClassItem getClassDetail(Long classId) {
        // 클래스 조회
        ClassRoom classRoom = classroomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        // 학생 수 조회
        Integer studentCount = classParticipantRepository.findByClassRoom_ClassId(classId).size();

        // 썸네일 경로
        String thumbnail = null;

        // 강사 이름
        String instructorName = classRoom.getUser() != null ? classRoom.getUser().getName() : null;

        return AdminClassResponse.ClassItem.from(classRoom, thumbnail, instructorName, studentCount);
    }

    @Override
    @Transactional
    public AdminClassResponse.StudentOperationResponse assignStudents(
            Long classId,
            AdminClassRequest.Students request) {

        // 클래스 조회
        ClassRoom classRoom = classroomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        List<ClassParticipant> newParticipants = new ArrayList<>();
        List<AdminClassResponse.StudentOperationResult> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (Long studentId : request.getStudentIds()) {
            AdminClassResponse.StudentOperationResult result;

            try {
                // 학생 조회
                User student = userRepository.findById(studentId)
                        .orElseThrow(() -> new CustomException(AdminStatus.USER_NOT_FOUND));

                // 학생 권한 확인
                if (!student.getRole().equals(UserRole.STUDENT)) {
                    result = AdminClassResponse.StudentOperationResult.builder()
                            .studentId(studentId)
                            .studentName(student.getName())
                            .success(false)
                            .message("학생 권한이 아닙니다")
                            .build();
                    failureCount++;
                    results.add(result);
                    continue;
                }

                // 이미 배정된 학생인지 확인
                Optional<ClassParticipant> existingParticipant = classParticipantRepository
                        .findByClassRoom_ClassIdAndUser_UserId(classId, studentId);

                if (existingParticipant.isPresent()) {
                    result = AdminClassResponse.StudentOperationResult.builder()
                            .studentId(studentId)
                            .studentName(student.getName())
                            .success(false)
                            .message("이미 배정된 학생입니다")
                            .build();
                    failureCount++;
                    results.add(result);
                    continue;
                }

                // 새로운 참여자 생성
                ClassParticipant participant = ClassParticipant.builder()
                        .classRoom(classRoom)
                        .user(student)
                        .build();

                newParticipants.add(participant);

                result = AdminClassResponse.StudentOperationResult.builder()
                        .studentId(studentId)
                        .studentName(student.getName())
                        .success(true)
                        .message("배정 성공")
                        .build();
                successCount++;
                results.add(result);

            } catch (CustomException e) {
                result = AdminClassResponse.StudentOperationResult.builder()
                        .studentId(studentId)
                        .studentName(null)
                        .success(false)
                        .message("존재하지 않는 사용자입니다")
                        .build();
                failureCount++;
                results.add(result);

            } catch (Exception e) {
                result = AdminClassResponse.StudentOperationResult.builder()
                        .studentId(studentId)
                        .studentName(null)
                        .success(false)
                        .message("배정 중 오류가 발생했습니다: " + e.getMessage())
                        .build();
                failureCount++;
                results.add(result);
                log.error("학생 배정 중 오류 발생 - studentId: {}, error: {}", studentId, e.getMessage());
            }
        }

        // 일괄 저장
        if (!newParticipants.isEmpty()) {
            classParticipantRepository.saveAll(newParticipants);
            log.info("학생 일괄 배정 완료 - classId: {}, 성공: {}명, 실패: {}명",
                    classId, successCount, failureCount);
        }

        return AdminClassResponse.StudentOperationResponse.builder()
                .totalCount(request.getStudentIds().size())
                .successCount(successCount)
                .failureCount(failureCount)
                .results(results)
                .build();
    }

    @Override
    @Transactional
    public AdminClassResponse.StudentOperationResponse deleteStudents(
            Long classId,
            AdminClassRequest.Students request) {

        // 클래스 조회
        ClassRoom classRoom = classroomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        List<ClassParticipant> participantsToDelete = new ArrayList<>();
        List<AdminClassResponse.StudentOperationResult> results = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (Long studentId : request.getStudentIds()) {
            AdminClassResponse.StudentOperationResult result;

            try {
                // 학생 조회
                User student = userRepository.findById(studentId)
                        .orElseThrow(() -> new CustomException(AdminStatus.USER_NOT_FOUND));

                // 학생 권한 확인
                if (!student.getRole().equals(UserRole.STUDENT)) {
                    result = AdminClassResponse.StudentOperationResult.builder()
                            .studentId(studentId)
                            .studentName(student.getName())
                            .success(false)
                            .message("학생 권한이 아닙니다")
                            .build();
                    failureCount++;
                    results.add(result);
                    continue;
                }

                // 배정된 학생인지 확인
                Optional<ClassParticipant> existingParticipant = classParticipantRepository
                        .findByClassRoom_ClassIdAndUser_UserId(classId, studentId);

                if (existingParticipant.isEmpty()) {
                    result = AdminClassResponse.StudentOperationResult.builder()
                            .studentId(studentId)
                            .studentName(student.getName())
                            .success(false)
                            .message("배정되지 않은 학생입니다")
                            .build();
                    failureCount++;
                    results.add(result);
                    continue;
                }

                // 삭제 목록에 추가
                participantsToDelete.add(existingParticipant.get());

                result = AdminClassResponse.StudentOperationResult.builder()
                        .studentId(studentId)
                        .studentName(student.getName())
                        .success(true)
                        .message("배정 해제 성공")
                        .build();
                successCount++;
                results.add(result);

            } catch (CustomException e) {
                result = AdminClassResponse.StudentOperationResult.builder()
                        .studentId(studentId)
                        .studentName(null)
                        .success(false)
                        .message("존재하지 않는 사용자입니다")
                        .build();
                failureCount++;
                results.add(result);

            } catch (Exception e) {
                result = AdminClassResponse.StudentOperationResult.builder()
                        .studentId(studentId)
                        .studentName(null)
                        .success(false)
                        .message("배정 해제 중 오류가 발생했습니다: " + e.getMessage())
                        .build();
                failureCount++;
                results.add(result);
                log.error("학생 배정 해제 중 오류 발생 - studentId: {}, error: {}", studentId, e.getMessage());
            }
        }

        // 일괄 삭제
        if (!participantsToDelete.isEmpty()) {
            classParticipantRepository.deleteAll(participantsToDelete);
            log.info("학생 일괄 배정 해제 완료 - classId: {}, 성공: {}명, 실패: {}명",
                    classId, successCount, failureCount);
        }

        return AdminClassResponse.StudentOperationResponse.builder()
                .totalCount(request.getStudentIds().size())
                .successCount(successCount)
                .failureCount(failureCount)
                .results(results)
                .build();
    }

    @Override
    public AdminPageResponse<AdminClassResponse.ClassStudentsResponse> getClassStudentsList(
            Long classId,
            AdminClassRequest.SearchFilter filter,
            int page,
            int pageSize) {

        // 클래스 존재 확인
        ClassRoom classRoom = classroomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        // 정렬 조건 생성
        Sort.Direction direction = filter.getSortDirection().equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        // createdAt으로 정렬 (등록일시 기준)
        Sort sort = Sort.by(direction, "createdAt");
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        // 학생 목록 조회 (페이징)
        Page<ClassParticipant> participantPage = classParticipantRepository
                .findByClassIdWithKeyword(classId, filter.getKeyword(), pageable);

        // DTO 변환
        Page<AdminClassResponse.ClassStudentsResponse> responsePage = participantPage
                .map(AdminClassResponse.ClassStudentsResponse::from);

        // AdminPageResponse로 변환하여 반환
        return AdminPageResponse.from(responsePage);
    }

    @Override
    public AdminPageResponse<AdminClassResponse.ClassItem> getClassList(
            AdminClassRequest.SearchFilter filter,
            int page,
            int size) {

        // 정렬 조건 생성
        Sort.Direction direction = filter.getSortDirection().equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, filter.getSortBy());
        Pageable pageable = PageRequest.of(page, size, sort);

        // 페이지 조회
        Page<ClassRoom> classPage = classroomRepository.findByKeywordAndStatusAndDateRange(
                filter.getKeyword(),
                filter.getStartDate(),
                filter.getEndDate(),
                pageable
        );

        // ClassItem으로 변환
        Page<AdminClassResponse.ClassItem> itemPage = classPage.map(classRoom -> {
            Integer studentCount = classParticipantRepository
                    .findByClassRoom_ClassId(classRoom.getClassId())
                    .size();

            String thumbnail = null;
            String instructorName = classRoom.getUser() != null
                    ? classRoom.getUser().getName()
                    : null;

            return AdminClassResponse.ClassItem.from(
                    classRoom,
                    thumbnail,
                    instructorName,
                    studentCount
            );
        });

        return AdminPageResponse.from(itemPage);
    }

    @Override
    public List<AdminClassResponse.UserSearchItem> searchInstructors(String keyword) {
        log.info("강사 검색: keyword={}", keyword);

        List<User> instructors = userRepository.searchInstructors(keyword);

        return instructors.stream()
                .map(AdminClassResponse.UserSearchItem::from)
                .toList();
    }

    @Override
    public List<AdminClassResponse.UserSearchItem> searchAvailableStudents(Long classId, String keyword) {
        log.info("클래스 미참여 학생 검색: classId={}, keyword={}", classId, keyword);

        // 클래스 존재 여부 확인
        ClassRoom classRoom = classroomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(AdminStatus.CLASS_NOT_FOUND));

        List<User> availableStudents = userRepository.searchAvailableStudents(classId, keyword);

        return availableStudents.stream()
                .map(AdminClassResponse.UserSearchItem::from)
                .toList();
    }

    // ==================== 엑셀 다운로드 ====================

    @Override
    public byte[] exportClassToExcel(Long classId) {
        log.info("클래스 엑셀 다운로드: classId={}", classId);

        // 클래스 조회
        ClassRoom classRoom = classroomRepository.findById(classId)
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        // 수강생 목록 조회
        List<ClassParticipant> participants = classParticipantRepository.findByClassIdWithUser(classId);

        try (Workbook workbook = new XSSFWorkbook()) {
            // 스타일 생성
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle infoLabelStyle = createInfoLabelStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // ===== 시트 1: 클래스 정보 =====
            Sheet infoSheet = workbook.createSheet("클래스 정보");
            createClassInfoSheet(infoSheet, classRoom, participants.size(), headerStyle, infoLabelStyle, dataStyle);

            // ===== 시트 2: 수강생 목록 =====
            Sheet studentSheet = workbook.createSheet("수강생 목록");
            createStudentListSheet(studentSheet, participants, headerStyle, dataStyle);

            // 바이트 배열로 변환
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("엑셀 파일 생성 실패: {}", e.getMessage());
            throw new CustomException(AdminStatus.USER_EXCEL_EXPORT_ERROR);
        }
    }

    @Override
    public byte[] exportClassListToExcel(AdminClassRequest.SearchFilter filter) {
        log.info("클래스 목록 엑셀 다운로드");

        Sort.Direction direction = filter.getSortDirection().equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, filter.getSortBy());

        List<ClassRoom> classList = classroomRepository.findAllByKeywordAndStatusAndDateRange(
                filter.getKeyword(),
                filter.getStartDate(),
                filter.getEndDate(),
                sort
        );

        try (Workbook workbook = new XSSFWorkbook()) {
            // 스타일 생성
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // 클래스 목록 시트 생성
            Sheet sheet = workbook.createSheet("클래스 목록");

            // 헤더 행 생성
            Row headerRow = sheet.createRow(0);
            String[] headers = {"번호", "클래스명", "담당 강사", "수업 유형", "시작일", "종료일",
                    "수업 시간", "수강생 수", "상태", "생성일"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 행 생성
            int rowNum = 1;
            for (ClassRoom classRoom : classList) {
                Row row = sheet.createRow(rowNum);

                Integer studentCount = classParticipantRepository
                        .findByClassRoom_ClassId(classRoom.getClassId())
                        .size();

                createCell(row, 0, rowNum, dataStyle);
                createCell(row, 1, classRoom.getTitle(), dataStyle);
                createCell(row, 2, classRoom.getUser() != null ? classRoom.getUser().getName() : "-", dataStyle);
                createCell(row, 3, classRoom.isOnline() ? "온라인" : "오프라인", dataStyle);
                createCell(row, 4, classRoom.getStartDate() != null ? classRoom.getStartDate().format(DATE_FORMATTER) : "-", dataStyle);
                createCell(row, 5, classRoom.getEndDate() != null ? classRoom.getEndDate().format(DATE_FORMATTER) : "-", dataStyle);
                createCell(row, 6, formatTimeRange(classRoom), dataStyle);
                createCell(row, 7, studentCount, dataStyle);
                createCell(row, 8, classRoom.isActive() ? "활성" : "비활성", dataStyle);
                createCell(row, 9, classRoom.getCreatedAt() != null ? classRoom.getCreatedAt().format(DATETIME_FORMATTER) : "-", dataStyle);

                rowNum++;
            }

            // 컬럼 너비 자동 조정
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // 최소 너비 설정
                if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000);
                }
            }

            // 바이트 배열로 변환
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("엑셀 파일 생성 실패: {}", e.getMessage());
            throw new CustomException(AdminStatus.USER_EXCEL_EXPORT_ERROR);
        }
    }

    // ==================== 엑셀 헬퍼 메서드 ====================

    private void createClassInfoSheet(Sheet sheet, ClassRoom classRoom, int studentCount,
                                      CellStyle headerStyle, CellStyle labelStyle, CellStyle dataStyle) {
        int rowNum = 0;

        // 제목
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("클래스 상세 정보");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

        rowNum++; // 빈 행

        // 클래스 정보
        createInfoRow(sheet, rowNum++, "클래스명", classRoom.getTitle(), labelStyle, dataStyle);
        createInfoRow(sheet, rowNum++, "담당 강사", classRoom.getUser() != null ? classRoom.getUser().getName() : "-", labelStyle, dataStyle);
        createInfoRow(sheet, rowNum++, "강사 이메일", classRoom.getUser() != null ? classRoom.getUser().getEmail() : "-", labelStyle, dataStyle);
        createInfoRow(sheet, rowNum++, "수업 유형", classRoom.isOnline() ? "온라인" : "오프라인", labelStyle, dataStyle);
        createInfoRow(sheet, rowNum++, "시작일", classRoom.getStartDate() != null ? classRoom.getStartDate().format(DATE_FORMATTER) : "-", labelStyle, dataStyle);
        createInfoRow(sheet, rowNum++, "종료일", classRoom.getEndDate() != null ? classRoom.getEndDate().format(DATE_FORMATTER) : "-", labelStyle, dataStyle);
        createInfoRow(sheet, rowNum++, "수업 시간", formatTimeRange(classRoom), labelStyle, dataStyle);
        createInfoRow(sheet, rowNum++, "수강생 수", String.valueOf(studentCount) + "명", labelStyle, dataStyle);
        createInfoRow(sheet, rowNum++, "상태", classRoom.isActive() ? "활성" : "비활성", labelStyle, dataStyle);
        createInfoRow(sheet, rowNum++, "설명", classRoom.getDescription() != null ? classRoom.getDescription() : "-", labelStyle, dataStyle);

        // 컬럼 너비 설정
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 10000);
    }

    private void createStudentListSheet(Sheet sheet, List<ClassParticipant> participants,
                                        CellStyle headerStyle, CellStyle dataStyle) {
        // 헤더 행 생성
        Row headerRow = sheet.createRow(0);
        String[] headers = {"번호", "이름", "이메일", "등록일"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 데이터 행 생성
        int rowNum = 1;
        for (ClassParticipant participant : participants) {
            Row row = sheet.createRow(rowNum);
            User student = participant.getUser();

            createCell(row, 0, rowNum, dataStyle);
            createCell(row, 1, student != null ? student.getName() : "-", dataStyle);
            createCell(row, 2, student != null ? student.getEmail() : "-", dataStyle);
            createCell(row, 3, participant.getCreatedAt() != null ? participant.getCreatedAt().format(DATETIME_FORMATTER) : "-", dataStyle);

            rowNum++;
        }

        // 컬럼 너비 자동 조정
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) < 3000) {
                sheet.setColumnWidth(i, 3000);
            }
        }
    }

    private void createInfoRow(Sheet sheet, int rowNum, String label, String value,
                               CellStyle labelStyle, CellStyle dataStyle) {
        Row row = sheet.createRow(rowNum);

        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(labelStyle);

        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(dataStyle);
    }

    private void createCell(Row row, int column, Object value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value != null ? value.toString() : "-");
        }
        cell.setCellStyle(style);
    }

    private String formatTimeRange(ClassRoom classRoom) {
        if (classRoom.getStartTime() == null || classRoom.getEndTime() == null) {
            return "-";
        }
        return classRoom.getStartTime().format(TIME_FORMATTER) + " ~ " + classRoom.getEndTime().format(TIME_FORMATTER);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createInfoLabelStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}