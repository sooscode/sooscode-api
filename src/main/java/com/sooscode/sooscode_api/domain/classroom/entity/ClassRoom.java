package com.sooscode.sooscode_api.domain.classroom.entity;


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

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;

    @Column(name = "max_students")
    @Builder.Default
    private Integer maxStudents = 30;

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

    public enum ClassStatus {
        UPCOMING, ONGOING, FINISHED
    }

    public enum ClassMode {
        READ, FREE, QUIZ
    }
}