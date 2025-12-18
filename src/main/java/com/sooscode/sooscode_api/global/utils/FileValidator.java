package com.sooscode.sooscode_api.global.utils;

import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.status.FileStatus;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * 파일 업로드 유효성 검증 유틸리티
 */
public class FileValidator {

    /**
     * 전체 업로드 데이터 검증 (컨트롤러나 서비스에서 호출)
     */
    public static void validateUploadData(String lectureDate, List<MultipartFile> files) {
        validateLectureDate(lectureDate);
        validateFiles(files);
    }


    /**
     * 클래스 ID 검증
     */
    public static void validateClassId(Long classId) {
        if (classId == null || classId <= 0) {
            throw new CustomException(FileStatus.INVALID_CLASS_ID);
        }
    }


    /**
     * 날짜 문자열 검증
     * (yyyy-MM-dd)
     */
    public static void validateLectureDate(String lectureDate) {

        if (lectureDate == null || lectureDate.trim().isEmpty()) {
            throw new CustomException(FileStatus.DATE_REQUIRED);
        }

        // yyyy-MM-dd 형식 검증
        if (!lectureDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new CustomException(FileStatus.DATE_FORMAT_INVALID);
        }
    }


    /**
     * 파일 리스트 검증
     */
    public static void validateFiles(List<MultipartFile> files) {

        if (files == null || files.isEmpty()) {
            throw new CustomException(FileStatus.REQUIRED);
        }

        // 파일 업로드 수 제한 (예: 최대 10개)
        if (files.size() > 10) {
            throw new CustomException(FileStatus.COUNT_EXCEEDED);
        }

        for (MultipartFile file : files) {
            validateSingleFile(file);
        }
    }


    /**
     * 단일 파일 검증
     */
    private static void validateSingleFile(MultipartFile file) {

        // null or empty
        if (file == null || file.isEmpty()) {
            throw new CustomException(FileStatus.EMPTY);
        }

        String filename = file.getOriginalFilename();

        if (filename == null || filename.trim().isEmpty()) {
            throw new CustomException(FileStatus.NAME_INVALID);
        }

        validateFileExtension(filename);
        validateFileSize(file);
    }


    /**
     * 확장자 검증 (허용되는 파일 형식만 업로드 가능)
     */
    private static void validateFileExtension(String filename) {

        String lower = filename.toLowerCase();

        if (!(lower.endsWith(".jpg") ||
                lower.endsWith(".jpeg") ||
                lower.endsWith(".png") ||
                lower.endsWith(".pdf") ||
                lower.endsWith(".txt") ||
                lower.endsWith(".zip"))) {

            throw new CustomException(FileStatus.INVALID_FILE_TYPE);
        }
    }


    /**
     * 파일 크기 검증 (최대 10MB)
     */
    private static void validateFileSize(MultipartFile file) {
        long maxSize = 10 * 1024 * 1024; // 10MB

        if (file.getSize() > maxSize) {
            throw new CustomException(FileStatus.FILE_SIZE_EXCEEDED);
        }
    }

    /**
     * 파일 삭제 요청 검증 (fileIds)
     */
    public static void validateDeleteFileIds(List<Long> fileIds) {

        if (fileIds == null || fileIds.isEmpty()) {
            throw new CustomException(FileStatus.INVALID_REQUEST);
        }

        for (Long id : fileIds) {
            if (id == null || id <= 0) {
                throw new CustomException(FileStatus.INVALID_REQUEST);
            }
        }
    }

    /**
     * 조회용 날짜 검증 + LocalDate 변환
     */
    public static LocalDate validateAndParseLectureDate(String lectureDate) {

        if (lectureDate == null || lectureDate.trim().isEmpty()) {
            throw new CustomException(FileStatus.DATE_REQUIRED);
        }

        if (!lectureDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new CustomException(FileStatus.DATE_FORMAT_INVALID);
        }

        try {
            return LocalDate.parse(lectureDate);
        } catch (Exception e) {
            throw new CustomException(FileStatus.DATE_FORMAT_INVALID);
        }
    }

}
