//package com.sooscode.sooscode_api.application.classroom.service;
//
//import com.sooscode.sooscode_api.domain.file.entity.SooFile;
//import com.sooscode.sooscode_api.domain.user.entity.User;
//import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
//import com.sooscode.sooscode_api.global.exception.CustomException;
//import com.sooscode.sooscode_api.global.exception.ErrorCode;
//import com.sooscode.sooscode_api.infra.file.S3FileService;
//import jakarta.persistence.EntityManager;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
///**
// *  user에 File 아이디 추가후 로직 검증 예정
// */
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class TestServiceImpl {
//
//    private final UserRepository userRepository;
//    private final S3FileService fileService;
//    private final EntityManager entityManager;
//
//    public void updateProfileImage(Long userId, MultipartFile photo) throws IOException {
//
//        // 1. 유저 조회
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
//
//        // CASE 1: 새 프로필 이미지 업로드
//        if (photo != null && !photo.isEmpty()) {
//
//            SooFile oldFile = user.getFileId();   // fileId 필드 기준
//
//            // (1) FK 제거 - null값을 넣어서 파일의 FK를 제거함
//            user.setFileId(null);
//            userRepository.save(user);
//            entityManager.flush();
//
//            // (2) 기존 파일 삭제
//            if (oldFile != null) {
//                fileService.deleteFile(oldFile);
//            }
//
//            // (3) 새 파일 업로드 후 연결
//            // 파일업로드에 성공하면 Key를 반환해서 유저의 메타데이터 등록
//            SooFile uploaded = fileService.uploadProfileImage(photo);
//            user.setFileId(uploaded);
//        }
//
//        // CASE 2: 프로필 이미지 완전 삭제 요청(photo == null)
//        else if (photo == null) {
//
//            SooFile oldFile = user.getFileId();
//
//            // (1) FK 제거
//            user.setFileId(null);
//            userRepository.save(user);
//            entityManager.flush();
//
//            // (2) 기존 파일 삭제
//            if (oldFile != null) {
//                fileService.deleteFile(oldFile);
//            }
//        }
//    }
//}
