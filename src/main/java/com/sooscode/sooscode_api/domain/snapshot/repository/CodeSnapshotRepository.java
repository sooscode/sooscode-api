package com.sooscode.sooscode_api.domain.snapshot.repository;

import com.sooscode.sooscode_api.domain.snapshot.entity.CodeSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeSnapshotRepository extends JpaRepository<CodeSnapshot, Long> {
}
