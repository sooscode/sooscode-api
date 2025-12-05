package com.sooscode.sooscode_api.application.auth.service;

import com.sooscode.sooscode_api.application.auth.dto.*;
import com.sooscode.sooscode_api.domain.user.entity.EmailCode;
import com.sooscode.sooscode_api.domain.user.entity.TempCredential;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import com.sooscode.sooscode_api.domain.user.repository.EmailCodeRepository;
import com.sooscode.sooscode_api.domain.user.repository.TempCredentialRepository;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.exception.CustomException;
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

import java.security.SecureRandom;
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
    private final TempCredentialRepository tempCredentialRepository;

    /**
     * 로그인 - 인증 및 JWT 토큰 생성
     */
    public LoginResponse authenticateAndGenerateTokens(
            LoginRequest request,
            AuthenticationManager authenticationManager
    ) {
        /**
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
         */
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        /**
         * JWT 토큰 생성
         */
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken);
    }

    /**
     * 회원가입 유효성
     */
    private void validateRegisterRequest(RegisterRequest request) {

        // 이메일 길이 체크
        if (request.getEmail() == null ||
                request.getEmail().length() < 5 ||
                request.getEmail().length() > 100) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "이메일은 5~100자여야 합니다.");
        }

        // 이메일 형식 체크
        if (!request.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "이메일 형식이 올바르지 않습니다.");
        }

        // 비밀번호 길이 체크
        if (request.getPassword() == null ||
                request.getPassword().length() < 8 ||
                request.getPassword().length() > 20) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "비밀번호는 8~20자여야 합니다.");
        }

        // 비밀번호 일치 확인
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "비밀번호가 일치하지 않습니다.");
        }

        // 이름 길이 체크
        if (request.getName() == null ||
                request.getName().length() < 2 ||
                request.getName().length() > 20) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "이름은 2~20자여야 합니다.");
        }

        // 이메일 인증 여부 검사
        EmailCode emailCode = emailCodeRepository
                .findTopByEmailOrderByEmailCodeIdDesc(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.VALIDATION_FAILED, "이메일 인증을 먼저 진행해주세요."));

        if (!emailCode.getIsVerified()) {
            throw new CustomException(ErrorCode.VALIDATION_FAILED, "이메일 인증이 완료되지 않았습니다.");
        }
    }


    /**
     * 회원가입
     */
    public RegisterResponse registerUser(RegisterRequest request) {

        validateRegisterRequest(request);

        if (userService.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.AUTH_DUPLICATE_EMAIL);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
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
                .findTopByEmailOrderByEmailCodeIdDesc(email)
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
     * 6자리 코드 랜덤 생성
     */
    private String generateCode(){
        return String.format("%06d", new Random().nextInt(999999));
    }

    /**
     * 이메일 html 템플릿
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
     * 임시 비밀번호 생성
     */
    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 10; i++) {   // 10자리
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }

    /**
     *
     * 임시 비밀번호 이메일 발송
     */
    public void sendTempPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        String tempPassword = generateTempPassword();

        TempCredential credential = new TempCredential();
        credential.setUser(user);
        credential.setTempPassword(tempPassword);
        credential.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        credential.setIsUsed(false);

        tempCredentialRepository.save(credential);

        sendTempPasswordEmail(email, tempPassword);
    }

    /**
     *
     * 임시 비밀번호 이메일 html 템플릿
     */
    private void sendTempPasswordEmail(String to, String tempPassword) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("[SOOSCODE] 임시 비밀번호 안내");

            String htmlContent = """
          <html>
          <body style="font-family: Arial, sans-serif; background:#f6f7fb; padding:20px;">
              <div style="max-width:480px; margin:0 auto; background:white; padding:24px; 
                          border-radius:12px; box-shadow:0 4px 10px rgba(0,0,0,0.1);">
                  
                  <h2 style="color:#5865f2; text-align:center;">임시 비밀번호 안내</h2>
                  
                  <p style="text-align:center; font-size:14px; color:#555;">
                      아래 임시 비밀번호로 로그인한 후,<br/>
                      반드시 새로운 비밀번호로 변경해주세요.
                  </p>

                  <div style="text-align:center; margin:30px 0;">
                      <span style="
                          display:inline-block; 
                          padding:12px 20px;
                          background:#e8eaff;
                          border:1px solid #5865f2;
                          border-radius:8px;
                          font-size:22px;
                          font-weight:700;
                          letter-spacing:4px;
                          color:#333;">
                          %s
                      </span>
                  </div>

                  <p style="font-size:12px; color:#777; text-align:center;">
                      이 임시 비밀번호는 발급 시점 기준 <b>30분간 유효</b>합니다.<br/>
                      비밀번호 재설정을 요청하지 않았다면 이 이메일은 무시해주세요.
                  </p>

                  <div style="margin-top:24px; text-align:center; font-size:11px; color:#aaa;">
                      © SOOSCODE Online Lecture Platform
                  </div>

              </div>
          </body>
          </html>
          """.formatted(tempPassword);

            helper.setText(htmlContent, true); // HTML 사용

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("임시 비밀번호 이메일 전송 오류", e);
        }
    }

    /**
     * 임시 비밀번호 로그인
     */
    public LoginResponse loginWithTempPassword(String email, String tempPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));

        TempCredential credential = tempCredentialRepository
                .findTopByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new RuntimeException("임시 비밀번호가 존재하지 않습니다."));

        if (credential.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("임시 비밀번호가 만료되었습니다.");
        }

        if (Boolean.TRUE.equals(credential.getIsUsed())) {
            throw new RuntimeException("이미 사용된 임시 비밀번호입니다.");
        }

        if (!credential.getTempPassword().equals(tempPassword)) {
            throw new RuntimeException("임시 비밀번호가 일치하지 않습니다.");
        }

        credential.setIsUsed(true);
        tempCredentialRepository.save(credential);

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken);
    }
}