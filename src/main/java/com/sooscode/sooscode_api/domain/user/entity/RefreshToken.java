package com.sooscode.sooscode_api.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {
    @Id
    @Column(name = "token_value")
    private String tokenValue;

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Column(nullable = false, name="expiration")
    private LocalDateTime expiredAt;

    @Column(nullable = false, name="created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean rememberMe;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
