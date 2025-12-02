package com.sooscode.sooscode_api.domain.file.repository;

import com.sooscode.sooscode_api.domain.file.entity.SooFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SooFileRepository extends JpaRepository<SooFile, Long> {
}
