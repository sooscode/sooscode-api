package com.sooscode.sooscode_api.application.auth.service;

import com.sooscode.sooscode_api.application.auth.dto.*;
import com.sooscode.sooscode_api.application.auth.util.CookieUtil;
import com.sooscode.sooscode_api.domain.user.entity.EmailCode;
import com.sooscode.sooscode_api.domain.user.entity.RefreshToken;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.AuthProvider;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import com.sooscode.sooscode_api.domain.user.repository.EmailCodeRepository;
import com.sooscode.sooscode_api.domain.user.repository.RefreshTokenRepository;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.api.exception.CustomException;
import com.sooscode.sooscode_api.application.admin.status.AuthStatus;
import com.sooscode.sooscode_api.global.jwt.JwtUtil;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.sooscode.sooscode_api.application.admin.status.AuthStatus.ERROR_WHILE_EMAIL_SENDING;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final EmailCodeRepository emailCodeRepository;
    private final JavaMailSender mailSender;
    private final RefreshTokenRepository refreshTokenRepository;
    private final GoogleAuthService googleAuthService;

    /**
     * 로그인 - 인증 및 JWT 토큰 생성
     */
    public LoginResponse authenticateAndGenerateTokens(
            LoginRequest request,
            AuthenticationManager authenticationManager,
            HttpServletResponse response
    ) {

        // 1. 유저 조회 (파일 포함)
        User user = userRepository.findByEmailWithFile(request.getEmail())
                .orElseThrow(() -> new CustomException(AuthStatus.EMAIL_NOT_FOUND));

        // 2. 비밀번호 인증
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Long userId = user.getUserId();

        // 3. Access Token 생성
        String accessToken = jwtUtil.generateAccessToken(user);

        // 4. Refresh Token 생성 or 조회
        String refreshToken;

        Optional<RefreshToken> existing = refreshTokenRepository.findByUserId(userId);
        if (existing.isPresent()) {
            refreshToken = existing.get().getTokenValue();
        } else {
            refreshToken = jwtUtil.generateRefreshToken(user);

            RefreshToken token = new RefreshToken();
            token.setTokenValue(refreshToken);
            token.setUserId(userId);
            token.setExpiredAt(LocalDateTime.now().plusDays(7));

            refreshTokenRepository.save(token);
        }

        // 5. 쿠키에 토큰 저장 (중첩 DTO 사용하지 않기 때문에 이 위치가 맞음)
        CookieUtil.addTokenCookies(response, new TokenResponse(accessToken, refreshToken));

        // 6. Body로 내려줄 평탄화된 유저 정보
        return new LoginResponse(
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                user.getProfileImage()
        );
    }


    /**
     * RT로 AT재발급
     */
    @Transactional
    public TokenResponse reissueAccessToken(String refreshToken) {
        RefreshToken savedToken =
                refreshTokenRepository.findByTokenValue(refreshToken)
                        .orElseThrow(() -> new CustomException(AuthStatus.REFRESH_TOKEN_NOT_FOUND));

        if (savedToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(AuthStatus.REFRESH_TOKEN_EXPIRED);
        }

        User user = userRepository.findById(savedToken.getUserId())
                .orElseThrow(() -> new CustomException(AuthStatus.LOGIN_FAILED));

        String newAccessToken = jwtUtil.generateAccessToken(user);

        /**
         *  RT는 변경하지 않음
         */
        return new TokenResponse(newAccessToken, savedToken.getTokenValue());
    }

    /**
     * RT 토큰 삭제
     */
    @Transactional
    public void deleteRefreshToken(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    /**
     * 회원가입
     */
    public RegisterResponse registerUser(RegisterRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProvider(AuthProvider.LOCAL);
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
     * 유저 상태값 확인
     */
    public boolean isInactiveEmail(String email) {
        return userRepository.findByEmailAndStatus(email, UserStatus.INACTIVE).isPresent();
    }

    /**
     * 이메일 중복 확인
     */
    public boolean isDuplicateActiveEmail(String email) {
        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE).isPresent();
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
        emailCode.setExpiredAt(LocalDateTime.now().plusMinutes(5));

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
        if (emailCode.getExpiredAt().isBefore(LocalDateTime.now())) return false;

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
            throw new CustomException(ERROR_WHILE_EMAIL_SENDING);
        }
    }

    /**
     * 임시 비밀번호 생성
     */
//    private String generateTempPassword() {
//        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
//        StringBuilder sb = new StringBuilder();
//        SecureRandom random = new SecureRandom();
//
//        for (int i = 0; i < 10; i++) {   // 10자리
//            int index = random.nextInt(chars.length());
//            sb.append(chars.charAt(index));
//        }
//
//        return sb.toString();
//    }

    /**
     *
     * 임시 비밀번호 이메일 발송
     */
//    public void sendTempPassword(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new CustomException(AuthErrorCode.LOGIN_FAILED));
//
//        String tempPassword = generateTempPassword();
//
//        TempCredential credential = new TempCredential();
//        credential.setUser(user);
//        credential.setTempPassword(tempPassword);
//        credential.setExpiresAt(LocalDateTime.now().plusMinutes(30));
//        credential.setIsUsed(false);
//
//        tempCredentialRepository.save(credential);
//
//        sendTempPasswordEmail(email, tempPassword);
//    }

    /**
     *
     * 임시 비밀번호 이메일 html 템플릿
     */
//    private void sendTempPasswordEmail(String to, String tempPassword) {
//
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setTo(to);
//            helper.setSubject("[SOOSCODE] 임시 비밀번호 안내");
//
//            String htmlContent = """
//          <html>
//          <body style="font-family: Arial, sans-serif; background:#f6f7fb; padding:20px;">
//              <div style="max-width:480px; margin:0 auto; background:white; padding:24px;
//                          border-radius:12px; box-shadow:0 4px 10px rgba(0,0,0,0.1);">
//
//                  <h2 style="color:#5865f2; text-align:center;">임시 비밀번호 안내</h2>
//
//                  <p style="text-align:center; font-size:14px; color:#555;">
//                      아래 임시 비밀번호로 로그인한 후,<br/>
//                      반드시 새로운 비밀번호로 변경해주세요.
//                  </p>
//
//                  <div style="text-align:center; margin:30px 0;">
//                      <span style="
//                          display:inline-block;
//                          padding:12px 20px;
//                          background:#e8eaff;
//                          border:1px solid #5865f2;
//                          border-radius:8px;
//                          font-size:22px;
//                          font-weight:700;
//                          letter-spacing:4px;
//                          color:#333;">
//                          %s
//                      </span>
//                  </div>
//
//                  <p style="font-size:12px; color:#777; text-align:center;">
//                      이 임시 비밀번호는 발급 시점 기준 <b>30분간 유효</b>합니다.<br/>
//                      비밀번호 재설정을 요청하지 않았다면 이 이메일은 무시해주세요.
//                  </p>
//
//                  <div style="margin-top:24px; text-align:center; font-size:11px; color:#aaa;">
//                      © SOOSCODE Online Lecture Platform
//                  </div>
//
//              </div>
//          </body>
//          </html>
//          """.formatted(tempPassword);
//
//            helper.setText(htmlContent, true); // HTML 사용
//
//            mailSender.send(message);
//
//        } catch (Exception e) {
//            throw new RuntimeException("임시 비밀번호 이메일 전송 오류", e);
//        }
//    }

    /**
     * 임시 비밀번호 로그인
     */
//    public LoginResponse loginWithTempPassword(String email, String tempPassword) {
//
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new CustomException(AuthErrorCode.LOGIN_FAILED));
//
//        TempCredential credential = tempCredentialRepository
//                .findTopByUserOrderByCreatedAtDesc(user)
//                .orElseThrow(() -> new RuntimeException("임시 비밀번호가 존재하지 않습니다."));
//
//        if (credential.getExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new RuntimeException("임시 비밀번호가 만료되었습니다.");
//        }
//
//        if (Boolean.TRUE.equals(credential.getIsUsed())) {
//            throw new RuntimeException("이미 사용된 임시 비밀번호입니다.");
//        }
//
//        if (!credential.getTempPassword().equals(tempPassword)) {
//            throw new RuntimeException("임시 비밀번호가 일치하지 않습니다.");
//        }
//
//        credential.setIsUsed(true);
//        tempCredentialRepository.save(credential);
//
//        String accessToken = jwtUtil.generateAccessToken(user);
//        String refreshToken = jwtUtil.generateRefreshToken(user);
//
//        return new LoginResponse(accessToken, refreshToken);
//    }

    /**
     * 구글 로그인 유저 정보 얻기
     */
    @Override
    public GoogleLoginResponse loginUserResponse(String code) {

        // 1. 구글 access_token 요청
        GoogleOAuthTokenDto tokenResponse = googleAuthService.getAccessToken(code);

        // 2. 구글 사용자 정보 조회
        GoogleUserDto googleUser = googleAuthService.getUserInfo(tokenResponse.accessToken());

        // 3. DB 유저 조회 or 신규 생성
        User user = userRepository.findByEmail(googleUser.email())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(googleUser.email())
                            .name(googleUser.name())
                            .provider(AuthProvider.GOOGLE)
                            .role(UserRole.STUDENT)
                            .status(UserStatus.ACTIVE)
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .build();
                    return userRepository.save(newUser);
                });

        // 4. AccessToken / RefreshToken 생성
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // 5. RefreshToken 저장
        RefreshToken token = new RefreshToken();
        token.setUserId(user.getUserId());
        token.setTokenValue(refreshToken);
        token.setExpiredAt(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(token);

        // 6. 로그인 응답 (Body용)
        LoginResponse userInfo = new LoginResponse(
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                user.getProfileImage()
        );

        // 7. 소셜 로그인 최종 Response
        return new GoogleLoginResponse(
                accessToken,
                refreshToken,
                userInfo
        );
    }

}