package com.sooscode.sooscode_api.domain.snapshot.repository;

import com.sooscode.sooscode_api.domain.snapshot.entity.CodeSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeSnapshotRepository extends JpaRepository<CodeSnapshot, Long> {
}
