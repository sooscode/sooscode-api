package com.sooscode.sooscode_api.application.chatting.controller;

import com.sooscode.sooscode_api.application.chatting.dto.ChatRoomDto;
import com.sooscode.sooscode_api.domain.chatting.service.ChatRoomMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatRoomMemberController {
    private final ChatRoomMemberService chatRoomMemberService;

    @PostMapping("/effectiveness")
    public ResponseEntity<?> effectiveness(@RequestBody Map<String, String> body) {
        String nickname = body.get("nickname");
        boolean exist = chatRoomMemberService.existsByNickname(nickname);
        if (exist) {
            return ResponseEntity.ok("존재합니다");
        } else {
            return ResponseEntity.badRequest().body("존재하지않는 닉네임입니다");
        }
    }
    @GetMapping("/api/chat/rooms")
    public ResponseEntity<?> getRooms(@RequestParam String nickname){
        List<ChatRoomDto> chatRoom =  chatRoomMemberService.findByNickname(nickname);
        return ResponseEntity.ok(chatRoom);


    }

}
