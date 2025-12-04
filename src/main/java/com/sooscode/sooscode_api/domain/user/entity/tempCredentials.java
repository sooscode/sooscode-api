//package com.sooscode.sooscode_api.domain.user.entity;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.sql.Timestamp;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "temp_credentials")
//@Getter
//@Setter
//public class TempCredentials {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "temp_credential_id")
//    private Long tempCredentialId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @Column(nullable = false, name = "user_id")
//    private User user;
//
//    @Column(nullable = false, name = "temp_password")
//    private String tempPassword;
//
//    @Column(name = "expires_at", nullable = false)
//    private LocalDateTime expiresAt;
//
//    @Column(name = "is_used")
//    private Boolean isUsed = false;
//
//    @Column(name = "issued_at")
//    private LocalDateTime issuedAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//}
//
