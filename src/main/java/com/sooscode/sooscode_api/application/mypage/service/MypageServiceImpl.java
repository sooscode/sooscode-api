package com.sooscode.sooscode_api.application.mypage.service;

import com.sooscode.sooscode_api.application.mypage.dto.MyClassResponse;
import com.sooscode.sooscode_api.application.mypage.dto.UpdatePasswordRequest;
import com.sooscode.sooscode_api.application.mypage.dto.UpdateProfileRequest;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassAssignment;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassParticipant;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassAssignmentRepository;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassParticipantRepository;
import com.sooscode.sooscode_api.domain.file.entity.SooFile;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.errorcode.UserErrorCode;
import com.sooscode.sooscode_api.infra.file.service.S3FileService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageServiceImpl implements MypageService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final S3FileService fileService;
    private final ClassParticipantRepository classParticipantRepository;
    private final ClassAssignmentRepository classAssignmentRepository;

    /**
     * 유저 조회
     */
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND));
    }

    /**
     * 비밀번호 변경
     */
    @Override
    @Transactional
    public void updatePassword(User user, UpdatePasswordRequest request) {

        // 현재 비밀번호 비교
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 암호화해서 저장
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    /**
     * 프로필 정보 변경
     */
    @Override
    public User updateProfile(User user, UpdateProfileRequest request) {
        /**
         * 변경하고 싶은 항목 아래 추가하면 됨
         */
        if (request.getName() != null) {
            user.setName(request.getName());
        }

        return userRepository.save(user);
    }

    /**
     * 회원 탈퇴 (status active -> inactive)
     */
    @Override
    public void deleteUser(User user) {
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }


    /**
     * 프로필 이미지 변경
     */
    @Transactional
    public void updateProfileImage(Long userId, MultipartFile photo) throws IOException {

        User user = getUser(userId);

        // 기존 파일이 있다면 일단 참조
        SooFile oldFile = user.getFile();

        // 파일이 업로드된 경우만 처리
        if (photo != null && !photo.isEmpty()) {

            // 기존 파일 삭제
            if (oldFile != null) {
                fileService.deleteFile(oldFile);
            }

            // 새 파일 업로드
            SooFile newFile = fileService.uploadProfileImage(photo);

            // 새로운 파일 연결
            user.setFile(newFile);
        }
    }


    /**
     * 프로필 이미지 삭제
     */
    @Transactional
    public void deleteProfileImage(Long userId) {

        User user = getUser(userId);
        SooFile oldFile = user.getFile();

        if (oldFile != null) {

            // 파일 삭제
            fileService.deleteFile(oldFile);

            // FK 해제
            user.setFile(null);
        }
    }


    /**
     * 프로필 이미지 조회
     */
//    public String getProfileImage(Long userId) {
//
//        User user = getUser(userId);
//
//        if (user.getFile() == null) {
//            return null; // 기본 이미지 사용
//        }
//
//        return user.getFile().getUrl();
//    }

    /**
     * 내가 참여하고 있는 클래스 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<MyClassResponse> getMyClasses(User user) {

        List<ClassParticipant> list =
                classParticipantRepository.findByUser(user);

        return list.stream()
            .map(p -> {

                ClassRoom cls = p.getClassRoom();

                // 강사 목록 조회
                List<ClassAssignment> assignments =
                        classAssignmentRepository.findByClassRoom(cls);

                List<String> teacherName = assignments.stream()
                        .map(a -> a.getUser().getName())
                        .collect(Collectors.toList());

                return new MyClassResponse(
                        cls.getClassId(),
                        cls.getTitle(),
                        teacherName,
                        p.getCreatedAt()
                );
            })
            .collect(Collectors.toList());
    }

}
