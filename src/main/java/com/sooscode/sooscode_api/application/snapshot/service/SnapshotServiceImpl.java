package com.sooscode.sooscode_api.application.snapshot.service;

import com.sooscode.sooscode_api.application.snapshot.dto.SnapshotLanguage;
import com.sooscode.sooscode_api.application.snapshot.dto.SnapshotTitleResponse;
import com.sooscode.sooscode_api.application.snapshot.dto.SnapShotResponse;
import com.sooscode.sooscode_api.application.snapshot.dto.SnapshotRequest;
import com.sooscode.sooscode_api.domain.classroom.entity.ClassRoom;
import com.sooscode.sooscode_api.domain.classroom.repository.ClassRoomRepository;
import com.sooscode.sooscode_api.domain.snapshot.entity.CodeSnapshot;
import com.sooscode.sooscode_api.domain.snapshot.repository.CodeSnapshotRepository;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.global.api.status.ClassRoomStatus;
import com.sooscode.sooscode_api.global.api.status.SnapshotStatus;
import com.sooscode.sooscode_api.global.api.status.UserStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SnapshotServiceImpl implements SnapshotService {

    private final CodeSnapshotRepository codeSnapshotRepository;
    private final UserRepository userRepository;
    private final ClassRoomRepository classRoomRepository;

    @Override
    public SnapShotResponse readSnapshot(Long userId, Long classId, Long snapshotId){
        CodeSnapshot snapshot = codeSnapshotRepository
                .findByCodeSnapshotIdAndUser_UserIdAndClassRoom_ClassId(
                        snapshotId, userId, classId
                )
                .orElseThrow(() -> new CustomException(SnapshotStatus.NOT_FOUND));

        return SnapShotResponse.from(snapshot);
    }

    @Override
    public CodeSnapshot saveCodeSnapshot(SnapshotRequest rq, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserStatus.NOT_FOUND));

        ClassRoom classRoom = classRoomRepository.findById(rq.getClassId())
                .orElseThrow(() -> new CustomException(ClassRoomStatus.CLASS_NOT_FOUND));

        CodeSnapshot codeSnapshot = CodeSnapshot.builder()
                .user(user)
                .classRoom(classRoom)
                .title(rq.getTitle())
                .content(rq.getContent())
                .language(rq.getLanguage())
                .updatedAt(rq.getUpdatedAt())
                .build();

        return codeSnapshotRepository.save(codeSnapshot);
    }

    @Override
    @Transactional
    public CodeSnapshot updateCodeSnapshot(SnapshotRequest rq, Long LoginuserId, Long snapshotId) {

        CodeSnapshot codeSnapshot = codeSnapshotRepository.findById(snapshotId)
                .orElseThrow(() -> new CustomException(SnapshotStatus.NOT_FOUND));

        if (!codeSnapshot.getUser().getUserId().equals(LoginuserId)) {
            throw new CustomException(SnapshotStatus.FORBIDDEN);
        }
        codeSnapshot.update(rq.getTitle(), rq.getContent());

        return codeSnapshotRepository.save(codeSnapshot); // 이건 됨
    }

    @Override
    public Page<SnapShotResponse> readAllSnapshots(Long userId, Long classId, Pageable pageable) {

        Page<CodeSnapshot> snapshots =
                codeSnapshotRepository
                        .findAllByUser_UserIdAndClassRoom_ClassId(userId, classId, pageable);

        return snapshots.map(SnapShotResponse::from);

    }

    @Override
    public List<SnapShotResponse> readSnapshotsByTitle(Long userId, Long classId, String title) {

        List<CodeSnapshot> snapshots =
                codeSnapshotRepository
                        .findByUser_UserIdAndClassRoom_ClassIdAndTitleContaining(userId, classId, title);

        return snapshots.stream()
                .map(SnapShotResponse::from)
                .toList();

    }

    @Override
    public List<SnapShotResponse> readSnapshotsByContent(Long userId, Long classId, String content) {

        List<CodeSnapshot> snapshots =
                codeSnapshotRepository
                        .findByUser_UserIdAndClassRoom_ClassIdAndContentContaining(userId, classId, content);

        return snapshots.stream()
                .map(SnapShotResponse::from)
                .toList();
    }

    @Override
    public List<SnapShotResponse> readSnapshotByDate(Long userId, Long classId, LocalDateTime start, LocalDateTime end) {

        List<CodeSnapshot> snapshots =
                codeSnapshotRepository
                        .findByUser_userIdAndClassRoom_classIdAndCreatedAtBetween(userId, classId, start, end);

        return snapshots.stream()
                .map(SnapShotResponse::from)
                .toList();
    }

    @Override
    public List<SnapShotResponse> readSnapshotByTitleAndDate(Long userId, Long classId, String title, LocalDateTime start, LocalDateTime end) {
        List<CodeSnapshot> snapshots =
                codeSnapshotRepository
                        .findByUser_UserIdAndClassRoom_ClassIdAndTitleContainingAndCreatedAtBetween(userId, classId, title, start, end);

        return snapshots.stream()
                .map(SnapShotResponse::from)
                .toList();
    }

    @Override
    public List<SnapShotResponse> readSnapshotByContentAndDate(Long userId, Long classId, String content, LocalDateTime start, LocalDateTime end) {
        List<CodeSnapshot> snapshots =
                codeSnapshotRepository
                        .findByUser_UserIdAndClassRoom_ClassIdAndContentContainingAndCreatedAtBetween(userId, classId, content, start, end);

        return snapshots.stream()
                .map(SnapShotResponse::from)
                .toList();
    }

    @Override
    public List<SnapshotTitleResponse> readTitleByDate(Long userId, Long classId, LocalDateTime start, LocalDateTime end) {

        return codeSnapshotRepository
                .findByUser_UserIdAndClassRoom_ClassIdAndCreatedAtBetween(
                        userId, classId, start, end)
                .stream()
                .map(s -> new SnapshotTitleResponse(
                        s.getCodeSnapshotId(),
                        s.getTitle(),
                        s.getLanguage(),
                        s.getCreatedAt()
                ))
                .toList();
    }
    @Override
    @Transactional
    public void deleteSnapshot(Long userId, Long classId, Long snapshotId){
        long deleted = codeSnapshotRepository
                .deleteByCodeSnapshotIdAndUser_UserIdAndClassRoom_ClassId(snapshotId, userId, classId);

        if (deleted == 0) {
           throw new CustomException(SnapshotStatus.NOT_FOUND);
        }

    }
    @Override
    public Page<SnapshotTitleResponse> readSnapshotByLanguageAndDate(Long userId, Long classId, SnapshotLanguage language, LocalDateTime start, LocalDateTime end, Pageable pageable){
        Page<CodeSnapshot> page = codeSnapshotRepository
                .findByUser_UserIdAndClassRoom_ClassIdAndLanguageAndCreatedAtBetween(
                        userId, classId, language, start, end, pageable
                );

        return page.map(s -> new SnapshotTitleResponse(
                s.getCodeSnapshotId(),
                s.getTitle(),
                s.getLanguage(),
                s.getCreatedAt()
        ));

    }
    @Override
    public Page<SnapshotTitleResponse> searchSnapshotTitles(
            Long userId,
            Long classId,
            SnapshotLanguage language,
            String title,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    ){
        return codeSnapshotRepository.searchSnapshots(
                userId,
                classId,
                language,
                title,
                start,
                end,
                pageable
        );
    }
}
