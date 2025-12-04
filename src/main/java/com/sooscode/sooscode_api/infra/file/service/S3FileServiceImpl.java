package com.sooscode.sooscode_api.infra.file.service;


import com.sooscode.sooscode_api.domain.file.entity.SooFile;
import com.sooscode.sooscode_api.domain.file.enums.FileType;
import com.sooscode.sooscode_api.domain.file.repository.SooFileRepository;
import com.sooscode.sooscode_api.infra.file.S3FileService;
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

    // ===== 간편한 업로드 메서드 =====
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

    // ===== 범용 업로드 =====
    @Override
    public SooFile uploadFile(MultipartFile multipartFile, FileType fileType) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        String directory = getDirectory(fileType);
        String fileKey = createFileKey(multipartFile, directory);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(multipartFile.getContentType())
                .build();

        // S3 업로드
        s3Client.putObject(request, RequestBody.fromBytes(multipartFile.getBytes()));

        // DB 저장
        return saveFileEntity(multipartFile, fileKey, fileType);
    }

    // ===== Public URL 제공 =====
    @Override
    public String getPublicUrl(Long fileId) {
        SooFile file = getFile(fileId);

        if (file.getFileType() == FileType.VIDEO)  {
            throw new RuntimeException("비디오는 Public URL을 제공하지 않습니다.");
        }

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName, region, file.getUrl());
    }

    // ===== Presigned URL 제공 =====
    @Override
    public String getPresignedUrl(Long fileId, int minutes) {
        SooFile file = getFile(fileId);

        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(file.getUrl())
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(minutes))
                .getObjectRequest(getRequest)
                .build();

        PresignedGetObjectRequest presigned = s3Presigner.presignGetObject(presignRequest);
        return presigned.url().toString();
    }

    // ===== S3 파일 삭제 =====
    @Override
    public void deleteFile(SooFile file) {
        if (file == null) return;

        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file.getUrl())
                    .build();

            s3Client.deleteObject(deleteRequest);
            fileRepository.delete(file);
        } catch (S3Exception e) {
            throw new RuntimeException("S3 파일 삭제 실패: " + file.getUrl(), e);
        }
    }

    // ===== 내부 메서드 =====
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

    private String createFileKey(MultipartFile file, String directory) {
        String originalName = file.getOriginalFilename();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        return directory + "/" + uuid + extension;
    }

    private SooFile saveFileEntity(MultipartFile multipartFile, String fileKey, FileType fileType) {
        String fileName = fileKey.substring(fileKey.lastIndexOf("/") + 1);

        SooFile entity = SooFile.builder()
                .originalName(multipartFile.getOriginalFilename())
                .storedName(fileName)
                .url(fileKey)
                .size(multipartFile.getSize())
                .type(multipartFile.getContentType())
                .fileType(fileType)
                .build();

        return fileRepository.save(entity);
    }

    private SooFile getFile(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
    }
}
