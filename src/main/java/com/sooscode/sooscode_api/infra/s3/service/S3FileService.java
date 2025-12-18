package com.sooscode.sooscode_api.infra.s3.service;

import com.sooscode.sooscode_api.domain.file.entity.SooFile;
import com.sooscode.sooscode_api.domain.file.enums.FileType;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

/**
 * S3 파일 업로드/조회/삭제 기능을 제공하는 서비스 인터페이스.
 */
public interface S3FileService {

    /**
     * 썸네일 이미지를 S3에 업로드한다.
     * @param file 업로드할 이미지 파일(MultipartFile)
     * @return 업로드된 파일 정보(SooFile 엔티티)
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    SooFile uploadThumbnail(MultipartFile file) throws IOException;

    /**
     * 사용자 프로필 이미지를 S3에 업로드한다.
     * @param file 업로드할 이미지 파일
     * @return 업로드된 파일 정보(SooFile)
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    SooFile uploadProfileImage(MultipartFile file) throws IOException;

    /**
     * 일반 이미지를 S3(public/images)에 업로드한다.
     * @param file 업로드할 이미지 파일
     * @return 업로드된 파일 정보(SooFile)
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    SooFile uploadImage(MultipartFile file) throws IOException;

    /**
     * 동영상 파일을 S3(private/videos)에 업로드한다.
     *
     * @param file 업로드할 동영상 파일
     * @return 업로드된 파일 정보(SooFile)
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    SooFile uploadVideo(MultipartFile file) throws IOException;

    /**
     * 파일 타입에 따라 지정된 S3 디렉토리에 파일을 업로드한다.
     *
     * @param multipartFile 업로드할 파일
     * @param fileType 파일 분류(THUMBNAIL, PROFILE_IMAGE, VIDEO 등)
     * @return 업로드된 파일 정보(SooFile)
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    SooFile uploadFile(MultipartFile multipartFile, FileType fileType) throws IOException;

    /**
     * S3 공개 경로(public)에 업로드된 파일의 Public URL을 반환한다.
     * (VIDEO 등 private 파일은 Public URL 조회 불가)
     *
     * @param fileId 조회할 파일 ID
     * @return S3 Public URL
     */
    String getPublicUrl(Long fileId);

    /**
     * S3 private 영역의 파일에 대해 일정 시간만 접근 가능한 Presigned URL을 생성한다.
     *
     * @param fileId 조회할 파일 ID
     * @param minutes URL 유효 시간(분)
     * @return Presigned URL
     */
    String getPresignedUrl(Long fileId, int minutes);

    /**
     * S3에 업로드된 파일을 삭제하고 DB에서도 파일 정보를 제거한다.
     *
     * @param file 삭제할 파일 엔티티
     */
    void deleteFile(SooFile file);
}
