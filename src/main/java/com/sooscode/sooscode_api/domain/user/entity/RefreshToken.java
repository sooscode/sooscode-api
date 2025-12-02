package com.sooscode.sooscode_api.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {
    @Id
    private String token_value;

    @Column(nullable = false)
    private Long user_id;

    @Column(nullable = false)
    private LocalDate expiration;

    @Column(nullable = false)
    private LocalDate created_at;
}
