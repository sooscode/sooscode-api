package com.sooscode.sooscode_api.domain.classroom.entity;

import com.sooscode.sooscode_api.domain.classroom.enums.ClassMode;
import com.sooscode.sooscode_api.domain.classroom.enums.ClassStatus;
import com.sooscode.sooscode_api.domain.file.entity.SooFile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "class_room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ClassRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id")
    private Long classId;

    @Column(name = "is_online", nullable = false)
    private boolean isOnline;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private SooFile file;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ClassStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private ClassMode mode;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at", nullable = false)
    private LocalDateTime endedAt;

    // ===== 비즈니스 로직 (상태 확인만, 예외 던지지 않음) =====

    /**
     * 온라인 클래스인지 확인
     */
    public boolean isOnlineClass() {
        return this.isOnline;
    }

    /**
     * 클래스가 시작되었는지 확인
     */
    public boolean isStarted() {
        return LocalDateTime.now().isAfter(this.startedAt) ||
                LocalDateTime.now().isEqual(this.startedAt);
    }

    /**
     * 클래스가 종료되었는지 확인
     */
    public boolean isEnded() {
        return LocalDateTime.now().isAfter(this.endedAt);
    }

    /**
     * 현재 접속 가능한지 확인 (단순 boolean 반환)
     */
    public boolean isAccessible() {
        return this.isOnline && isStarted() && !isEnded();
    }

    // ===== Setter 대신 의미있는 메서드 =====

    /**
     * 클래스 상태 변경
     */
    public void updateStatus(ClassStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * 클래스 모드 변경
     */
    public void updateMode(ClassMode newMode) {
        this.mode = newMode;
    }
}