package com.sooscode.sooscode_api.application.auth.service;

import com.sooscode.sooscode_api.application.auth.dto.*;
import com.sooscode.sooscode_api.domain.user.entity.EmailCode;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import com.sooscode.sooscode_api.domain.user.repository.EmailCodeRepository;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.exception.ErrorCode;
import com.sooscode.sooscode_api.global.jwt.JwtUtil;
import com.sooscode.sooscode_api.global.user.CustomUserDetails;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final EmailCodeRepository emailCodeRepository;
    private final JavaMailSender mailSender;

    /**
     * 로그인 - 인증 및 JWT 토큰 생성
     */
    public LoginResponse authenticateAndGenerateTokens(
            LoginRequest request,
            AuthenticationManager authenticationManager
    ) {/**
         * 스프링 시큐리티를 통한 인증
         */
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        /**
         * 인증된 사용자 정보 추출
         */CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        /**
         * JWT 토큰 생성
         */
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken);
    }

    /**
     * 회원가입
     */
    public RegisterResponse registerUser(RegisterRequest request) {

        if (userService.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.AUTH_DUPLICATE_EMAIL);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setProvider("local");
        user.setRole(UserRole.STUDENT);
        user.setStatus(UserStatus.ACTIVE);

        User newUser = userService.saveUser(user);

        return new RegisterResponse(
                newUser.getUserId(),
                newUser.getEmail(),
                newUser.getName(),
                newUser.getRole()
        );
    }

    /**
     * 이메일 중복 확인
     */
     public boolean isDuplicateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 이메일 인증 코드 발송
     */
    public void sendVerificationCode(String email) {
        String code = generateCode();

        EmailCode emailCode = new EmailCode();
        emailCode.setEmail(email);
        emailCode.setCode(code);
        emailCode.setIsVerified(false);
        emailCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        emailCodeRepository.save(emailCode);

        sendEmail(email, code);
    }

    /**
     * 인증 코드 검증
     */
    public boolean verifyEmailCode(String email, String code) {
        EmailCode emailCode = emailCodeRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElse(null);

        if (emailCode == null) return false;
        if (emailCode.getIsVerified()) return false;
        if (emailCode.getExpiresAt().isBefore(LocalDateTime.now())) return false;

        boolean isMatch = emailCode.getCode().equals(code);

        if(isMatch) {
            emailCode.setIsVerified(true);
            emailCodeRepository.save(emailCode);
        }

        return isMatch;
    }

    /**
     * 6자리 코드 랜덤 발송
     */
    private String generateCode(){
        return String.format("%06d", new Random().nextInt(999999));
    }

    /**
     * 이메일 전송
     */
    private void sendEmail(String to, String code) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("[SOOSCODE] 이메일 인증 코드");

            String htmlContent = """
              <!DOCTYPE html>
              <html lang="ko">
              <head>
                  <meta charset="UTF-8">
                  <title>SOOSCODE 이메일 인증</title>
                  <style>
                      body {
                          font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, "Noto Sans", sans-serif;
                          background-color: #f4f4f4;
                          margin: 0;
                          padding: 0;
                      }
                      .container {
                          max-width: 480px;
                          margin: 40px auto;
                          background-color: #ffffff;
                          border-radius: 12px;
                          padding: 24px 28px;
                          box-shadow: 0 4px 12px rgba(0,0,0,0.06);
                      }
                      .logo {
                          font-size: 22px;
                          font-weight: 700;
                          color: #5865f2;
                          text-align: center;
                          margin-bottom: 16px;
                      }
                      .title {
                          font-size: 18px;
                          font-weight: 600;
                          margin-bottom: 12px;
                          text-align: center;
                      }
                      .desc {
                          font-size: 14px;
                          color: #555;
                          line-height: 1.6;
                          margin-bottom: 24px;
                          text-align: center;
                      }
                      .code-box {
                          text-align: center;
                          margin: 20px 0 24px;
                      }
                      .code {
                          display: inline-block;
                          font-size: 26px;
                          letter-spacing: 6px;
                          font-weight: 700;
                          color: #333;
                          padding: 12px 20px;
                          border-radius: 8px;
                          background-color: #e8eaff;
                          border: 1px solid #5865f2;
                      }
                      .info {
                          font-size: 12px;
                          color: #888;
                          line-height: 1.5;
                          text-align: center;
                      }
                      .footer {
                          margin-top: 28px;
                          font-size: 11px;
                          color: #aaa;
                          text-align: center;
                          border-top: 1px solid #eee;
                          padding-top: 12px;
                      }
                  </style>
              </head>
              <body>
                  <div class="container">
                      <div class="logo">SOOSCODE</div>
                      <div class="title">이메일 인증코드를 발송했습니다.</div>
                      <p class="desc">
                          아래 인증코드를 회원가입 화면에 입력하고<br/>
                          이메일 인증을 완료해주세요.
                      </p>
                      <div class="code-box">
                           <span class="code">%s</span>
                      </div>
                      <p class="info">
                          · 본 인증코드는 발급 시점으로부터 <b>5분간만 유효</b>합니다.<br/>
                          · 인증을 요청하지 않으셨다면 이 메일은 무시하셔도 됩니다.
                      </p>
                      <div class="footer">
                          © SOOSCODE Online Lecture Platform
                      </div>
                  </div>
              </body>
              </html>
              """.formatted(code);

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("이메일 전송 중 오류 발생", e);
        }
    }

    /**
     * 임시 비밀번호 생성 & 저장
     */
//    public void sendTempPassword(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));
//
//        // 1) 랜덤 임시 비밀번호 생성
//        String tempPassword = RandomStringUtils.randomAlphanumeric(10);
//
//        // 2) temp_credentials 테이블에 저장
//        TempCredential credential = new TempCredential(
//                user.getUserId(),
//                tempPassword,
//                LocalDateTime.now().plusMinutes(30),
//                false
//        );
//        tempCredentialRepository.save(credential);
//
//        // 3) 이메일 발송 (생략)
//        mailService.sendTempPassword(email, tempPassword);
//    }


    /**
     * 임시 비밀번호 로그인
     */
//    public LoginResponse loginWithTempPassword(String email, String tempPassword) {
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));
//
//        TempCredential credential = tempCredentialRepository
//                .findTopByUserIdOrderByIssuedAtDesc(user.getUserId())
//                .orElseThrow(() -> new RuntimeException("임시 비밀번호가 존재하지 않습니다."));
//
//        // 만료 확인
//        if (credential.getExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new RuntimeException("임시 비밀번호가 만료되었습니다.");
//        }
//
//        // 이미 사용 됐는지
//        if (credential.isUsed()) {
//            throw new RuntimeException("이미 사용된 임시 비밀번호입니다.");
//        }
//
//        // 비밀번호 매칭
//        if (!credential.getTempPassword().equals(tempPassword)) {
//            throw new RuntimeException("임시 비밀번호가 일치하지 않습니다.");
//        }
//
//        // 성공 → 1회성 사용 처리
//        credential.setUsed(true);
//        tempCredentialRepository.save(credential);
//
//        // 토큰 발급
//        String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getRole());
//        String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), user.getRole());
//
//        return new LoginResponse(accessToken, refreshToken);
//    }

}