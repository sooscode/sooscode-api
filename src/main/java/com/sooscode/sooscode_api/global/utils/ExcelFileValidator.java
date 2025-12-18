package com.sooscode.sooscode_api.global.utils;

import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.status.AdminStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Slf4j
public class ExcelFileValidator {

    // 허용된 Excel MIME 타입
    private static final Set<String> ALLOWED_EXCEL_MIME_TYPES = Set.of(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",  // .xlsx
            "application/vnd.ms-excel"  // .xls (구버전)
    );

    // 최대 파일 크기 (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    // 최대 처리 행 수
    private static final int MAX_ROW_COUNT = 1000;

    // Excel 파일 시그니처 (매직 바이트)
    private static final byte[] XLSX_SIGNATURE = new byte[]{0x50, 0x4B, 0x03, 0x04};  // ZIP 기반 (xlsx)
    private static final byte[] XLS_SIGNATURE = new byte[]{(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0};  // OLE2 (xls)

    /**
     * Excel 파일 종합 검증
     */
    public static void validateExcelFile(MultipartFile file) {
        validateNotEmpty(file);
        validateFileSize(file);
        validateFileName(file);
//        validateMimeType(file);
        validateExcelSignature(file);
        validateExcelStructure(file);
    }

    /**
     * 파일 비어있는지 확인
     */
    private static void validateNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(AdminStatus.USER_EXCEL_EMPTY);
        }
    }

    /**
     * 파일 크기 검증
     */
    private static void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("파일 크기 초과: {}bytes (최대 {}bytes)", file.getSize(), MAX_FILE_SIZE);
            throw new CustomException(AdminStatus.USER_EXCEL_SIZE_EXCEEDED);
        }
    }

    /**
     * 파일명 검증 (경로 조작 방지)
     */
    private static void validateFileName(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (filename == null || filename.isBlank()) {
            throw new CustomException(AdminStatus.USER_EXCEL_INVALID_NAME);
        }

        // 경로 조작 문자 체크
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            log.warn("의심스러운 파일명: {}", filename);
            throw new CustomException(AdminStatus.USER_EXCEL_INVALID_NAME);
        }

        // 확장자 체크
        String lowerName = filename.toLowerCase();
        if (!lowerName.endsWith(".xlsx") && !lowerName.endsWith(".xls")) {
            log.warn("허용되지 않은 확장자: {}", filename);
            throw new CustomException(AdminStatus.USER_EXCEL_INVALID_EXTENSION);
        }
    }

    /**
     * MIME 타입 검증
     */
    private static void validateMimeType(MultipartFile file) {
        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_EXCEL_MIME_TYPES.contains(contentType.toLowerCase())) {
            log.warn("허용되지 않은 MIME 타입: {}", contentType);
            throw new CustomException(AdminStatus.USER_EXCEL_INVALID_TYPE);
        }
    }

    /**
     * Excel 파일 시그니처 검증
     */
    private static void validateExcelSignature(MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();

            if (fileBytes.length < 4) {
                throw new CustomException(AdminStatus.USER_EXCEL_INVALID_TYPE);
            }

            boolean isValidSignature = startsWith(fileBytes, XLSX_SIGNATURE) || startsWith(fileBytes, XLS_SIGNATURE);

            if (!isValidSignature) {
                log.warn("잘못된 파일 시그니처: {}", file.getOriginalFilename());
                throw new CustomException(AdminStatus.USER_EXCEL_DANGEROUS_FILE);
            }
        } catch (IOException e) {
            log.error("파일 읽기 실패", e);
            throw new CustomException(AdminStatus.USER_EXCEL_PARSE_ERROR);
        }
    }

    /**
     * Excel 구조 검증 (실제로 파싱 가능한지)
     */
    private static void validateExcelStructure(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            if (sheet == null) {
                throw new CustomException(AdminStatus.USER_EXCEL_INVALID_STRUCTURE);
            }

            // 헤더 row 확인
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new CustomException(AdminStatus.USER_EXCEL_INVALID_HEADER);
            }

            // 헤더 형식 검증 (email, name, role 필수)
            boolean hasEmail = false;
            boolean hasName = false;
            boolean hasRole = false;

            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    String header = cell.getStringCellValue().trim().toLowerCase();
                    if (header.equals("email")) hasEmail = true;
                    if (header.equals("name")) hasName = true;
                    if (header.equals("role")) hasRole = true;
                }
            }

            if (!hasEmail || !hasName || !hasRole) {
                log.warn("필수 헤더 누락 - email: {}, name: {}, role: {}", hasEmail, hasName, hasRole);
                throw new CustomException(AdminStatus.USER_EXCEL_INVALID_HEADER);
            }

            // 데이터 행 수 검증
            int rowCount = sheet.getLastRowNum();  // 0-based index (헤더 포함)

            if (rowCount < 1) {
                throw new CustomException(AdminStatus.USER_EXCEL_NO_DATA);
            }

            // 실제 데이터가 있는지 확인 (빈 행만 있을 수 있음)
            boolean hasData = false;
            for (int i = 1; i <= rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        Cell cell = row.getCell(j);
                        if (cell != null && cell.getCellType() != CellType.BLANK) {
                            String value = getCellValueAsString(cell);
                            if (!value.isBlank()) {
                                hasData = true;
                                break;
                            }
                        }
                    }
                    if (hasData) break;
                }
            }

            if (!hasData) {
                throw new CustomException(AdminStatus.USER_EXCEL_NO_DATA);
            }

            if (rowCount > MAX_ROW_COUNT) {
                log.warn("최대 행 수 초과: {} (최대 {})", rowCount, MAX_ROW_COUNT);
                throw new CustomException(AdminStatus.USER_EXCEL_TOO_MANY_ROWS);
            }

        } catch (CustomException e) {
            throw e;  // CustomException은 그대로 던짐
        } catch (Exception e) {
            log.error("Excel 파싱 실패", e);
            throw new CustomException(AdminStatus.USER_EXCEL_PARSE_ERROR);
        }
    }

    /**
     * 셀 값을 문자열로 변환
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue().trim();
                } catch (Exception e) {
                    yield "";
                }
            }
            default -> "";
        };
    }

    /**
     * 바이트 배열 시작 부분 비교
     */
    private static boolean startsWith(byte[] source, byte[] prefix) {
        if (source.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (source[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }
}