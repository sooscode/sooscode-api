package com.sooscode.sooscode_api.infra.s3.service;

import com.sooscode.sooscode_api.domain.file.entity.SooFile;
import com.sooscode.sooscode_api.domain.file.enums.FileType;
import com.sooscode.sooscode_api.domain.file.repository.SooFileRepository;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.status.S3Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class S3FileServiceImpl implements S3FileService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final SooFileRepository fileRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Override
    public SooFile uploadThumbnail(MultipartFile file) throws IOException {
        return uploadFile(file, FileType.THUMBNAIL);
    }

    @Override
    public SooFile uploadProfileImage(MultipartFile file) throws IOException {
        return uploadFile(file, FileType.PROFILE_IMAGE);
    }

    @Override
    public SooFile uploadImage(MultipartFile file) throws IOException {
        return uploadFile(file, FileType.IMAGE);
    }

    @Override
    public SooFile uploadVideo(MultipartFile file) throws IOException {
        return uploadFile(file, FileType.VIDEO);
    }

    @Override
    public SooFile uploadFile(MultipartFile multipartFile, FileType fileType) throws IOException {
        // 파일 유효성 검증
        validateFile(multipartFile);

        try {
            // S3 업로드 경로 생성
            String directory = getDirectory(fileType);
            String fileKey = createFileKey(multipartFile, directory);

            // S3 업로드 요청 생성
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(multipartFile.getContentType())
                    .build();

            // S3에 파일 업로드
            s3Client.putObject(request, RequestBody.fromBytes(multipartFile.getBytes()));
            log.info("S3 파일 업로드 성공: {}", fileKey);

            // DB에 파일 정보 저장
            return saveFileEntity(multipartFile, fileKey, fileType);

        } catch (S3Exception e) {
            log.error("S3 업로드 중 에러 발생: {}", e.awsErrorDetails().errorMessage(), e);
            throw new CustomException(S3Status.UPLOAD_FAILED, e.awsErrorDetails().errorMessage());
        } catch (IOException e) {
            log.error("파일 처리 중 에러 발생: {}", e.getMessage(), e);
            throw new CustomException(S3Status.UPLOAD_FAILED, "파일 읽기 실패");
        } catch (Exception e) {
            log.error("예상치 못한 에러 발생: {}", e.getMessage(), e);
            throw new CustomException(S3Status.CONNECTION_FAILED, e.getMessage());
        }
    }

    @Override
    public String getPublicUrl(Long fileId) {
        // 파일 조회
        SooFile file = getFile(fileId);

        // VIDEO 타입은 Public URL 제공 불가
        if (file.getFileType() == FileType.VIDEO) {
            log.warn("VIDEO 파일에 대한 Public URL 요청: fileId={}", fileId);
            throw new CustomException(S3Status.PUBLIC_URL_NOT_ALLOWED);
        }

        // Public URL 생성
        String publicUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, region, file.getUrl());

        log.info("Public URL 생성 완료: fileId={}", fileId);
        return publicUrl;
    }

    @Override
    public String getPresignedUrl(Long fileId, int minutes) {
        // 파일 조회
        SooFile file = getFile(fileId);

        try {
            // GetObject 요청 생성
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file.getUrl())
                    .build();

            // Presigned URL 요청 생성
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(minutes))
                    .getObjectRequest(getRequest)
                    .build();

            // Presigned URL 생성
            PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignRequest);
            String presignedUrl = presigned.url().toString();

            log.info("Presigned URL 생성 완료: fileId={}, 유효시간={}분", fileId, minutes);
            return presignedUrl;

        } catch (S3Exception e) {
            log.error("Presigned URL 생성 실패: {}", e.awsErrorDetails().errorMessage(), e);
            throw new CustomException(S3Status.PRESIGNED_URL_FAILED, e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            log.error("Presigned URL 생성 중 예상치 못한 에러: {}", e.getMessage(), e);
            throw new CustomException(S3Status.PRESIGNED_URL_FAILED, e.getMessage());
        }
    }

    @Override
    public void deleteFile(SooFile file) {
        // null 체크
        if (file == null) {
            log.warn("삭제할 파일이 null입니다");
            return;
        }

        try {
            // S3 삭제 요청 생성
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file.getUrl())
                    .build();

            // S3에서 파일 삭제
            s3Client.deleteObject(deleteRequest);
            log.info("S3 파일 삭제 성공: {}", file.getUrl());

            // DB에서 파일 정보 삭제
            fileRepository.delete(file);
            log.info("DB 파일 정보 삭제 완료: fileId={}", file.getFileId());

        } catch (S3Exception e) {
            log.error("S3 파일 삭제 실패: {}", e.awsErrorDetails().errorMessage(), e);
            throw new CustomException(S3Status.DELETE_FAILED, file.getUrl());
        } catch (Exception e) {
            log.error("파일 삭제 중 예상치 못한 에러: {}", e.getMessage(), e);
            throw new CustomException(S3Status.DELETE_FAILED, e.getMessage());
        }
    }

    // ===== 내부 헬퍼 메서드 =====

    /**
     * 파일 유효성 검증
     */
    private void validateFile(MultipartFile file) {
        // null 또는 empty 체크
        if (file == null || file.isEmpty()) {
            log.warn("업로드 파일이 null 또는 비어있음");
            throw new CustomException(S3Status.FILE_EMPTY);
        }

        // 파일 크기 체크 (100MB 제한)
        long maxSize = 100 * 1024 * 1024; // 100MB
        if (file.getSize() > maxSize) {
            log.warn("파일 크기 초과: {} bytes (최대: {} bytes)", file.getSize(), maxSize);
            throw new CustomException(S3Status.INVALID_FILE_TYPE, "파일 크기가 100MB를 초과합니다");
        }

        // 파일명 체크
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            log.warn("파일명이 유효하지 않음");
            throw new CustomException(S3Status.INVALID_FILE_TYPE, "파일명이 유효하지 않습니다");
        }

        // 확장자 체크
        if (!originalName.contains(".")) {
            log.warn("파일 확장자가 없음: {}", originalName);
            throw new CustomException(S3Status.INVALID_FILE_TYPE, "파일 확장자가 없습니다");
        }
    }

    /**
     * FileType에 따른 S3 디렉토리 경로 반환
     */
    private String getDirectory(FileType fileType) {
        return switch (fileType) {
            case VIDEO -> "private/videos";
            case THUMBNAIL -> "public/thumbnails";
            case PROFILE_IMAGE -> "public/profiles";
            case IMAGE -> "public/images";
            case LECTURE_MATERIAL -> "private/materials";
            case DOCUMENT -> "private/documents";
        };
    }

    /**
     * 고유한 파일 키(경로) 생성
     */
    private String createFileKey(MultipartFile file, String directory) {
        String originalName = file.getOriginalFilename();

        // 확장자 추출
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }

        // UUID로 고유 파일명 생성
        String uuid = UUID.randomUUID().toString();
        return directory + "/" + uuid + extension;
    }

    /**
     * 파일 엔티티를 DB에 저장
     */
    private SooFile saveFileEntity(MultipartFile multipartFile, String fileKey, FileType fileType) {
        // 저장할 파일명 추출
        String fileName = fileKey.substring(fileKey.lastIndexOf("/") + 1);

        // SooFile 엔티티 생성
        SooFile entity = SooFile.builder()
                .originalName(multipartFile.getOriginalFilename())
                .storedName(fileName)
                .url(fileKey)
                .size(multipartFile.getSize())
                .type(multipartFile.getContentType())
                .fileType(fileType)
                .build();

        SooFile saved = fileRepository.save(entity);
        log.info("파일 정보 DB 저장 완료: fileId={}", saved.getFileId());

        return saved;
    }

    /**
     * 파일 ID로 파일 엔티티 조회
     */
    private SooFile getFile(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> {
                    log.error("파일을 찾을 수 없음: fileId={}", fileId);
                    return new CustomException(S3Status.FILE_NOT_FOUND);
                });
    }
}