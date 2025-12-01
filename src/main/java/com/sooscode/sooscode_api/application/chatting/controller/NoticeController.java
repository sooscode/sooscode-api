package com.sooscode.sooscode_api.application.chatting.controller;

import com.sooscode.sooscode_api.domain.chatting.entity.ChatMessage;
import com.sooscode.sooscode_api.domain.chatting.entity.Notice;
import com.sooscode.sooscode_api.domain.chatting.repository.ChatMessageRepository;
import com.sooscode.sooscode_api.domain.chatting.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = { "http://localhost:5173", "http://10.41.0.89:5173" })
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeRepository noticeRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 🔥 공지 등록 API
    @PostMapping
    public Notice createNotice(@RequestBody Notice notice) {
        notice.setCreatedAt(LocalDateTime.now());

        // 1) 공지 자체를 DB에 저장
        Notice saved = noticeRepository.save(notice);

        // 2) 채팅방에 뿌릴 "공지용 채팅 메시지" 생성
        ChatMessage noticeMsg = new ChatMessage();
        noticeMsg.setSender("공지사항"); // 👈 프론트에서 구분하기 좋게
        // 제목 + 내용 중에 원하는 형태로
        noticeMsg.setText("[공지] " + saved.getTitle() + " - " + saved.getContent());
        noticeMsg.setCreatedAt(LocalDateTime.now());

        // 3) 이 공지 메시지도 ChatMessage 테이블에 저장 (히스토리에서 보이게)
        ChatMessage savedMsg = chatMessageRepository.save(noticeMsg);

        // 4) 현재 접속 중인 모든 사용자에게 브로드캐스트
        messagingTemplate.convertAndSend("/topic/chat", savedMsg);

        // 5) 응답은 공지 자체를 반환
        return saved;
    }

    // 🔎 필요하면 공지 목록 조회 API도 같이 만들 수 있음
    @GetMapping
    public List<Notice> getNotices() {
        return noticeRepository.findAll();
    }
}
