package com.sooscode.sooscode_api.domain.user.entity;

import com.sooscode.sooscode_api.domain.file.entity.SooFile;
import com.sooscode.sooscode_api.domain.user.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import java.time.LocalDateTime;

@Entity
@Table(name ="user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(updatable = false, nullable = false, name="created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false, name="updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private SooFile file;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 프로필 이미지
     */
    public String getProfileImage() {
        try {
            if (this.file != null) {
                return this.file.getUrl();
            }
        } catch (Exception e) {
            // Lazy loading 실패 시 기본 이미지 반환
            System.err.println("LazyInitializationException in getProfileImage: " + e.getMessage());
        }
        return "https://sooscode-s3file.s3.ap-northeast-2.amazonaws.com/profile.png";
    }
}

