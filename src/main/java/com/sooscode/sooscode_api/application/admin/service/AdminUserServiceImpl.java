package com.sooscode.sooscode_api.application.admin.service;

import com.sooscode.sooscode_api.application.admin.dto.AdminPageResponse;
import com.sooscode.sooscode_api.application.admin.dto.AdminUserRequest;
import com.sooscode.sooscode_api.application.admin.dto.AdminUserResponse;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassParticipantRepository;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.AuthProvider;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.AdminStatus;
import com.sooscode.sooscode_api.global.api.status.AuthStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClassParticipantRepository classParticipantRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // ==================== 계정 생성 ====================

    @Override
    @Transactional
    public AdminUserResponse.UserCreated createUser(AdminUserRequest.Create request, UserRole role) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(AuthStatus.DUPLICATE_EMAIL);
        }

        // 임시 비밀번호 생성
        String temporaryPassword = generateTemporaryPassword();
        String encodedPassword = passwordEncoder.encode(temporaryPassword);

        // User 엔티티 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .provider(AuthProvider.LOCAL)
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();

        User saved = userRepository.save(user);
        log.info("사용자 계정 생성 완료: userId={}, email={}, role={}",
                saved.getUserId(), saved.getEmail(), saved.getRole());

        // TODO: 이메일 전송 서비스 연동
        // emailService.sendUserCredentials(saved.getEmail(), saved.getName(), temporaryPassword);

        return AdminUserResponse.UserCreated.from(saved, temporaryPassword);
    }

    // ==================== 목록 조회 ====================

    @Override
    public AdminPageResponse<AdminUserResponse.UserListItem> getUserList(
            AdminUserRequest.SearchFilter filter,
            int page,
            int size
    ) {
        // 정렬 설정
        Sort sort = createSort(filter.getSortBy(), filter.getSortDirection());
        Pageable pageable = PageRequest.of(page, size, sort);

        // @Query 기반 동적 쿼리
        Page<User> userPage = userRepository.findByFilter(
                filter.getKeyword(),
                filter.getRole(),
                filter.getStatus(),
                filter.getStartDate(),
                filter.getEndDate(),
                pageable
        );

        // DTO 변환
        Page<AdminUserResponse.UserListItem> resultPage = userPage.map(user ->
                AdminUserResponse.UserListItem.from(user, null, null)
        );

        return AdminPageResponse.from(resultPage);
    }

    // ==================== 상세 조회 ====================

    @Override
    public AdminUserResponse.Detail getUserDetail(Long userId) {
        User user = findUserById(userId);
        return AdminUserResponse.Detail.from(user);
    }

    // ==================== 히스토리 조회 (Redis 연동 전 빈 리스트 반환) ====================

    @Override
    public List<AdminUserResponse.UserHistory> getUserHistory(Long userId, int limit) {
        // Redis 연동 전까지 빈 리스트 반환
        findUserById(userId); // 사용자 존재 여부 확인
        return List.of();
    }

    // ==================== 계정 삭제 (Soft Delete) ====================

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = findUserById(userId);

        // 관리자 계정 삭제 방지
        if (user.getRole() == UserRole.ADMIN) {
            throw new CustomException(AdminStatus.USER_FORBIDDEN_ADMIN_DELETE);
        }

        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);

        log.info("사용자 계정 비활성화: userId={}, email={}", userId, user.getEmail());
    }

    // ==================== 계정 활성화 ====================

    @Override
    @Transactional
    public void activeUser(Long userId) {
        User user = findUserById(userId);

        // 관리자 계정 상태 변경 방지
        if (user.getRole() == UserRole.ADMIN) {
            throw new CustomException(AdminStatus.USER_FORBIDDEN_ADMIN_MODIFY);
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        log.info("사용자 계정 활성화: userId={}, email={}", userId, user.getEmail());
    }

    // ==================== 역할 변경 ====================

    @Override
    @Transactional
    public void changeUserRole(Long userId, AdminUserRequest.ChangeRole request) {
        User user = findUserById(userId);

        // 관리자로 역할 변경 방지
        if (request.getRole() == UserRole.ADMIN) {
            throw new CustomException(AdminStatus.USER_FORBIDDEN_ROLE_TO_ADMIN);
        }

        // 관리자의 역할 변경 방지
        if (user.getRole() == UserRole.ADMIN) {
            throw new CustomException(AdminStatus.USER_FORBIDDEN_ADMIN_MODIFY);
        }

        UserRole previousRole = user.getRole();
        user.setRole(request.getRole());
        userRepository.save(user);

        log.info("사용자 역할 변경: userId={}, {} -> {}", userId, previousRole, request.getRole());
    }

    // ==================== 일괄 생성 (Excel) ====================

    @Override
    @Transactional
    public byte[] bulkCreateUsers(AdminUserRequest.BulkCreate request) {
        List<BulkCreateResult> results = new ArrayList<>();

        try (InputStream is = request.getCsvFile().getInputStream();
             Workbook inputWorkbook = new XSSFWorkbook(is)) {

            Sheet inputSheet = inputWorkbook.getSheetAt(0);

            // 헤더에서 email, name, role 컬럼 인덱스 찾기
            Row headerRow = inputSheet.getRow(0);
            int emailIdx = -1;
            int nameIdx = -1;
            int roleIdx = -1;

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null) {
                    String header = cell.getStringCellValue().trim().toLowerCase();
                    if (header.equals("email")) emailIdx = i;
                    if (header.equals("name")) nameIdx = i;
                    if (header.equals("role")) roleIdx = i;
                }
            }

            // 필수 컬럼 확인
            if (emailIdx == -1 || nameIdx == -1 || roleIdx == -1) {
                throw new CustomException(AdminStatus.USER_EXCEL_PARSE_ERROR);
            }

            // 데이터 행 처리 (1번 행부터)
            for (int rowNum = 1; rowNum <= inputSheet.getLastRowNum(); rowNum++) {
                Row row = inputSheet.getRow(rowNum);

                if (row == null) continue;

                String email = getCellValueAsString(row.getCell(emailIdx));
                String name = getCellValueAsString(row.getCell(nameIdx));
                String roleStr = getCellValueAsString(row.getCell(roleIdx));

                // 빈 행 스킵
                if (email.isBlank() && name.isBlank() && roleStr.isBlank()) continue;

                BulkCreateResult result = new BulkCreateResult();
                result.email = email;
                result.name = name;
                result.roleStr = roleStr;

                // 유효성 검사
                String validationError = validateUserData(email, name, roleStr);
                if (validationError != null) {
                    result.success = false;
                    result.errorMessage = validationError;
                    results.add(result);
                    continue;
                }

                try {
                    // Role enum 변환
                    UserRole role = UserRole.valueOf(roleStr.toUpperCase());

                    // 관리자 권한으로 생성 시도 방지
                    if (role == UserRole.ADMIN) {
                        result.success = false;
                        result.errorMessage = "잘못된 권한을 입력하셨습니다.";
                        results.add(result);
                        continue;
                    }

                    // 이메일 중복 체크
                    if (userRepository.existsByEmail(email)) {
                        result.success = false;
                        result.errorMessage = "이미 가입된 이메일입니다.";
                        results.add(result);
                        continue;
                    }

                    // 임시 비밀번호 생성 및 사용자 생성
                    String temporaryPassword = generateTemporaryPassword();
                    String encodedPassword = passwordEncoder.encode(temporaryPassword);

                    User user = User.builder()
                            .email(email)
                            .password(encodedPassword)
                            .name(name)
                            .provider(AuthProvider.LOCAL)
                            .role(role)
                            .status(UserStatus.ACTIVE)
                            .build();

                    userRepository.save(user);

                    result.success = true;
                    result.temporaryPassword = temporaryPassword;
                    results.add(result);

                    // TODO: 이메일 전송
                    // emailService.sendUserCredentials(email, name, temporaryPassword);

                } catch (Exception e) {
                    result.success = false;
                    result.errorMessage = "생성 실패: " + e.getMessage();
                    results.add(result);
                }
            }

        } catch (IOException e) {
            log.error("Excel 파일 읽기 실패", e);
            throw new CustomException(AdminStatus.USER_EXCEL_PARSE_ERROR);
        }

        // 결과를 엑셀로 생성
        return generateResultExcel(results);
    }

    /**
     * 유효성 검사
     */
    private String validateUserData(String email, String name, String roleStr) {
        // 이메일 빈 값 체크
        if (email == null || email.isBlank()) {
            return "이메일을 입력해주세요.";
        }

        // 이메일 형식 체크
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "올바른 이메일 형식이 아닙니다.";
        }

        // 이름 빈 값 체크
        if (name == null || name.isBlank()) {
            return "이름을 입력해주세요.";
        }

        // 역할 빈 값 체크
        if (roleStr == null || roleStr.isBlank()) {
            return "권한을 입력해주세요.";
        }

        // 역할 값 유효성 체크
        try {
            UserRole.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return "잘못된 권한을 입력하셨습니다.";
        }

        return null;
    }

    /**
     * 일괄 생성 결과를 엑셀로 생성
     */
    private byte[] generateResultExcel(List<BulkCreateResult> results) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("일괄 생성 결과");

            // 헤더 스타일
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            // 성공 스타일 (연한 초록색)
            CellStyle successStyle = workbook.createCellStyle();
            successStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            successStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 실패 스타일 (연한 빨간색)
            CellStyle failStyle = workbook.createCellStyle();
            failStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
            failStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 헤더 row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"이메일", "이름", "권한", "임시 비밀번호", "성공 여부", "비고"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 rows
            int rowNum = 1;
            for (BulkCreateResult result : results) {
                Row row = sheet.createRow(rowNum);

                row.createCell(0).setCellValue(result.email);
                row.createCell(1).setCellValue(result.name);
                row.createCell(2).setCellValue(result.roleStr);
                row.createCell(3).setCellValue(result.temporaryPassword != null ? result.temporaryPassword : "-");

                Cell successCell = row.createCell(4);
                successCell.setCellValue(result.success ? "성공" : "실패");
                successCell.setCellStyle(result.success ? successStyle : failStyle);

                row.createCell(5).setCellValue(result.errorMessage != null ? result.errorMessage : "-");

                rowNum++;
            }

            // 열 너비 자동 조정
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1024); // 약간의 여유 공간
            }

            workbook.write(out);

            long successCount = results.stream().filter(r -> r.success).count();
            long failureCount = results.size() - successCount;
            log.info("일괄 계정 생성 완료: 성공={}, 실패={}", successCount, failureCount);

            return out.toByteArray();

        } catch (IOException e) {
            log.error("결과 엑셀 파일 생성 실패", e);
            throw new CustomException(AdminStatus.USER_EXCEL_EXPORT_ERROR);
        }
    }

    /**
     * 일괄 생성 결과를 담는 내부 클래스
     */
    private static class BulkCreateResult {
        String email;
        String name;
        String roleStr;
        String temporaryPassword;
        boolean success;
        String errorMessage;
    }

    /**
     * 셀 값을 문자열로 변환
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    // ==================== 엑셀 다운로드 ====================

    @Override
    public byte[] exportUsersToExcel(AdminUserRequest.SearchFilter filter) {
        // @Query 기반 필터링된 사용자 전체 조회
        List<User> users = userRepository.findByFilterAll(
                filter.getKeyword(),
                filter.getRole(),
                filter.getStatus(),
                filter.getStartDate(),
                filter.getEndDate()
        );

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("사용자 목록");

            // 헤더 스타일
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            // 헤더 row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"번호", "이름", "이메일", "구분", "가입일"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 데이터 rows
            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(rowNum);  // 번호 (1부터 시작)
                row.createCell(1).setCellValue(user.getName());
                row.createCell(2).setCellValue(user.getEmail());
                row.createCell(3).setCellValue(formatRole(user.getRole()));
                row.createCell(4).setCellValue(user.getCreatedAt().format(DATE_FORMATTER));
                rowNum++;
            }

            // 열 너비 자동 조정
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 512); // 약간의 여유 공간
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            log.error("엑셀 파일 생성 실패", e);
            throw new CustomException(AdminStatus.USER_EXCEL_EXPORT_ERROR);
        }
    }

    // ==================== CSV 템플릿 다운로드 ====================

    /**
     * 일괄 생성용 엑셀 템플릿 생성
     */
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("사용자 등록");

            // 헤더 스타일
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            // 헤더 row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"email", "name", "role"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 예시 데이터 1 - 학생
            Row exampleRow1 = sheet.createRow(1);
            exampleRow1.createCell(0).setCellValue("student@example.com");
            exampleRow1.createCell(1).setCellValue("홍길동");
            exampleRow1.createCell(2).setCellValue("STUDENT");

            // 예시 데이터 2 - 강사
            Row exampleRow2 = sheet.createRow(2);
            exampleRow2.createCell(0).setCellValue("instructor@example.com");
            exampleRow2.createCell(1).setCellValue("김강사");
            exampleRow2.createCell(2).setCellValue("INSTRUCTOR");

            // 열 너비 조정
            sheet.setColumnWidth(0, 25 * 256);  // email
            sheet.setColumnWidth(1, 12 * 256);  // name
            sheet.setColumnWidth(2, 12 * 256);  // role

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            log.error("엑셀 템플릿 생성 실패", e);
            throw new CustomException(AdminStatus.USER_EXCEL_EXPORT_ERROR);
        }
    }

    // ==================== 헬퍼 메서드 ====================

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AdminStatus.USER_NOT_FOUND));
    }

    private Sort createSort(String sortBy, String sortDirection) {
        // 허용된 정렬 필드
        String field = switch (sortBy) {
            case "name" -> "name";
            case "email" -> "email";
            default -> "createdAt";
        };

        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(direction, field);
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    private String formatRole(UserRole role) {
        return switch (role) {
            case ADMIN -> "관리자";
            case INSTRUCTOR -> "강사";
            case STUDENT -> "학생";
        };
    }

    // ==================== 수강 클래스 목록 조회 ====================

    @Override
    public List<AdminUserResponse.EnrolledClass> getUserEnrolledClasses(Long userId) {
        // 사용자 존재 여부 확인
        User user = findUserById(userId);

        log.info("유저 수강 클래스 조회: userId={}, userName={}", userId, user.getName());

        // 해당 유저가 참가 중인 클래스 목록 조회 (ClassRoom과 강사 정보 JOIN)
        List<ClassParticipant> participants = classParticipantRepository
                .findByUserIdWithClassAndInstructor(userId);

        // DTO 변환
        List<AdminUserResponse.EnrolledClass> enrolledClasses = participants.stream()
                .map(AdminUserResponse.EnrolledClass::from)
                .toList();

        log.info("유저 수강 클래스 조회 완료: userId={}, classCount={}", userId, enrolledClasses.size());

        return enrolledClasses;
    }
}