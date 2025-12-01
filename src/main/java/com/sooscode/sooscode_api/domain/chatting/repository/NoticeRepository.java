package com.sooscode.sooscode_api.domain.chatting.repository;

import com.sooscode.sooscode_api.domain.chatting.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
