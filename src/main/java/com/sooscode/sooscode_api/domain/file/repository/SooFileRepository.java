package com.sooscode.sooscode_api.domain.file.repository;

import com.sooscode.sooscode_api.domain.file.entity.SooFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;

public interface SooFileRepository extends JpaRepository<SooFile, Long> {
}
