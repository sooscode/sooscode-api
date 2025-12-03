package com.sooscode.sooscode_api.domain.file.entity;


import com.sooscode.sooscode_api.domain.file.enums.FileType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "file")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SooFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long fileId;

    @Column(name = "original_name", length = 255, nullable = false)
    private String originalName;

    @Column(name = "stored_name", length = 255, nullable = false)
    private String storedName;

    @Column(name = "url", length = 500, nullable = false)
    private String url;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "type", length = 50, nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", length = 30, nullable = false)
    private FileType fileType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}