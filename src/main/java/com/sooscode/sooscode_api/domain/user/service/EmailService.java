//package com.sooscode.sooscode_api.domain.user.service;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import lombok.RequiredArgsConstructor;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import com.sooscode.sooscode_api.domain.user.entity.EmailCode;
//import com.sooscode.sooscode_api.domain.user.repository.EmailCodeRepository;
//
//import java.time.LocalDateTime;
//import java.util.Random;
//
//@Service
//@RequiredArgsConstructor
//public class EmailService {
//
//    private final JavaMailSender mailSender;
//    private final EmailCodeRepository emailCodeRepository;
//
//    public void sendVerificationCode(String email) {
//        String code = generateCode();
//        saveCode(email, code);
//
//        try {
//            sendHtmlMail(email, code);
//        } catch (MessagingException e) {
//            // 필요하면 여기서 로깅하고 커스텀 예외 던져도 됨
//            throw new RuntimeException("이메일 전송 중 오류 발생", e);
//        }
//    }
//
//    private void sendHtmlMail(String email, String code) throws MessagingException {
//        MimeMessage mimeMessage = mailSender.createMimeMessage();
//
//        // true = multipart 메일 허용, UTF-8 설정
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//
//        helper.setTo(email);
//        helper.setSubject("[SOOSCODE] 이메일 인증코드 안내");
//
//        String html = buildVerificationHtml(code);
//
//        helper.setText(html, true);
//
//        mailSender.send(mimeMessage);
//    }
//
//    private String buildVerificationHtml(String code) {
//        return """
//                <!DOCTYPE html>
//                <html lang="ko">
//                <head>
//                    <meta charset="UTF-8">
//                    <title>SOOSCODE 이메일 인증</title>
//                    <style>
//                        body {
//                            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans", sans-serif;
//                            background-color: #f4f4f4;
//                            margin: 0;
//                            padding: 0;
//                        }
//                        .container {
//                            max-width: 480px;
//                            margin: 40px auto;
//                            background-color: #ffffff;
//                            border-radius: 12px;
//                            padding: 24px 28px;
//                            box-shadow: 0 4px 12px rgba(0,0,0,0.06);
//                        }
//                        .logo {
//                            font-size: 22px;
//                            font-weight: 700;
//                            color: #5865f2;
//                            text-align: center;
//                            margin-bottom: 16px;
//                        }
//                        .title {
//                            font-size: 18px;
//                            font-weight: 600;
//                            margin-bottom: 12px;
//                            text-align: center;
//                        }
//                        .desc {
//                            font-size: 14px;
//                            color: #555;
//                            line-height: 1.6;
//                            margin-bottom: 24px;
//                            text-align: center;
//                        }
//                        .code-box {
//                            text-align: center;
//                            margin: 20px 0 24px;
//                        }
//                        .code {
//                            display: inline-block;
//                            font-size: 26px;
//                            letter-spacing: 6px;
//                            font-weight: 700;
//                            color: #333;
//                            padding: 12px 20px;
//                            border-radius: 8px;
//                            background-color: #e8eaff;
//                            border: 1px solid #5865f2;
//                        }
//                        .info {
//                            font-size: 12px;
//                            color: #888;
//                            line-height: 1.5;
//                            text-align: center;
//                        }
//                        .footer {
//                            margin-top: 28px;
//                            font-size: 11px;
//                            color: #aaa;
//                            text-align: center;
//                            border-top: 1px solid #eee;
//                            padding-top: 12px;
//                        }
//                    </style>
//                </head>
//                <body>
//                    <div class="container">
//                        <div class="logo">SOOSCODE</div>
//                        <div class="title">이메일 인증코드를 .</div>
//                        <p class="desc">
//                            아래 인증코드를 회원가입 화면에 입력하고<br/>
//                            이메일 인증을 완료해주세요.
//                        </p>
//                        <div class="code-box">
//                            <span class="code">%s</span>
//                        </div>
//                        <p class="info">
//                            · 본 인증코드는 발급 시점으로부터 <b>5분간만 유효</b>합니다.<br/>
//                            · 인증을 요청하지 않으셨다면 이 메일은 무시하셔도 됩니다.
//                        </p>
//                        <div class="footer">
//                            © SOOSCODE Online Lecture Platform
//                        </div>
//                    </div>
//                </body>
//                </html>
//                """.formatted(code);
//    }
//
//    private void saveCode(String email, String code) {
//        EmailCode entity = new EmailCode();
//        entity.setEmail(email);
//        entity.setCode(code);
//        entity.setVerified(false);
//        entity.setCreatedAt(LocalDateTime.now());
//        entity.setExpiresAt(LocalDateTime.now().plusMinutes(10));
//
//        emailCodeRepository.save(entity);
//    }
//
//    private String generateCode() {
//        return String.format("%06d", new Random().nextInt(1000000));
//    }
//}
//
