//package com.sooscode.sooscode_api.domain.user.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import com.sooscode.sooscode_api.domain.user.service.EmailService;
//import com.sooscode.sooscode_api.domain.user.service.EmailVerificationService;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor
//public class EmailAuthController {
//
//    private final EmailService emailService;
//    private final EmailVerificationService verificationService;
//
//    @PostMapping("/email/send")
//    public ResponseEntity<?> send(@RequestBody Map<String, String> req) {
//        emailService.sendVerificationCode(req.get("email"));
//        return ResponseEntity.ok("코드 발송 완료");
//    }
//
//    @PostMapping("/email/verify")
//    public ResponseEntity<?> verify(@RequestBody Map<String, String> req) {
//        boolean result = verificationService.verify(req.get("email"), req.get("code"));
//        return ResponseEntity.ok(Map.of("verified", result));
//    }
//}
