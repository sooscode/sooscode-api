package com.sooscode.sooscode_api.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_code")
@Getter
@Setter
public class EmailCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailCodeId;

    private String email;
    private String code;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
