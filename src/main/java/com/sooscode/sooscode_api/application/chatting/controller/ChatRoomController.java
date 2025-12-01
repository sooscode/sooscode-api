package com.sooscode.sooscode_api.application.chatting.controller;

import com.sooscode.sooscode_api.domain.chatting.entity.ChatRoom;
import com.sooscode.sooscode_api.domain.chatting.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = { "http://localhost:5173", "http://10.41.0.89:5173" })
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class ChatRoomController {

    private final ChatRoomRepository roomRepository;

    // 🔹 방 목록 조회 (1번, 2번, 3번, 4번...)
    @GetMapping
    public List<ChatRoom> getRooms() {
        return roomRepository.findAll();
    }

    // 🔹 방 생성 (새 채팅방 추가 버튼 눌렀을 때)
    @PostMapping
    public ChatRoom createRoom() {
        ChatRoom room = new ChatRoom();
        room.setCreatedAt(LocalDateTime.now());
        return roomRepository.save(room);  // 여기서 id 자동생성 (1,2,3,4,..)
    }
}
