package com.sooscode.sooscode_api.application.auth.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sooscode.sooscode_api.application.auth.dto.GoogleLoginResponse;
import com.sooscode.sooscode_api.application.auth.dto.GoogleOAuthTokenDto;
import com.sooscode.sooscode_api.application.auth.dto.GoogleUserDto;
import com.sooscode.sooscode_api.application.auth.dto.LoginResponse;
import com.sooscode.sooscode_api.domain.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.AuthProvider;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;
import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.status.AuthStatus;
import com.sooscode.sooscode_api.infra.security.jwt.JwtUtil;
import com.sooscode.sooscode_api.infra.redis.TokenRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenRedisService tokenRedisService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 필요한 3개만 @Value로 받음 */
    @Value("${oauth.google.client-id}")
    private String clientId;

    @Value("${oauth.google.client-secret}")
    private String clientSecret;

    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    /** URL은 고정 사용 */
    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USERINFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";


    /**
     * 구글 로그인 URL 생성
     */
    @Override
    public String buildGoogleLoginUrl() {

        String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

        return AUTH_URL
                + "?client_id=" + clientId
                + "&redirect_uri=" + encodedRedirectUri
                + "&response_type=code"
                + "&scope=" + URLEncoder.encode("openid email profile", StandardCharsets.UTF_8)
                + "&access_type=offline"
                + "&prompt=consent";
    }


    /**
     * Authorization Code → Access Token 요청
     */
    @Override
    public GoogleOAuthTokenDto getAccessToken(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(TOKEN_URL, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(AuthStatus.GOOGLE_ACCESS_TOKEN_FAILED);
        }

        try {
            JsonNode json = objectMapper.readTree(response.getBody());

            return new GoogleOAuthTokenDto(
                    json.get("access_token").asText(),
                    json.get("expires_in").asText(),
                    json.get("scope").asText(),
                    json.get("token_type").asText(),
                    json.has("id_token") ? json.get("id_token").asText() : null
            );

        } catch (Exception e) {
            throw new CustomException(AuthStatus.GOOGLE_ACCESS_TOKEN_FAILED);
        }
    }


    /**
     * Access Token → Google 사용자 정보 조회
     */
    @Override
    public GoogleUserDto getUserInfo(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(USERINFO_URL, HttpMethod.GET, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new CustomException(AuthStatus.GOOGLE_USER_INFO_FAILED);
        }

        try {
            JsonNode json = objectMapper.readTree(response.getBody());

            return new GoogleUserDto(
                    json.get("email").asText(),
                    json.get("name").asText(),
                    json.get("picture").asText()
            );

        } catch (Exception e) {
            throw new CustomException(AuthStatus.GOOGLE_USER_INFO_FAILED);
        }
    }

    /**
     * 구글 로그인 유저 정보 얻기
     */
    @Override
    public GoogleLoginResponse loginUserResponse(String code) {

        // 1. 구글 access_token 요청
        GoogleOAuthTokenDto tokenResponse = getAccessToken(code);

        // 2. 구글 사용자 정보 조회
        GoogleUserDto googleUser = getUserInfo(tokenResponse.accessToken());

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

        // ========== 기존: DB 저장 ==========
        // String refreshToken = jwtUtil.generateRefreshToken(user);
        // RefreshToken token = new RefreshToken();
        // token.setUserId(user.getUserId());
        // token.setTokenValue(refreshToken);
        // token.setExpiredAt(LocalDateTime.now().plusDays(7));
        // refreshTokenRepository.save(token);

        // ========== 변경: Redis 저장 ==========
        String refreshToken = tokenRedisService.getRefreshToken(user.getUserId());

        if (refreshToken == null || jwtUtil.isTokenExpired(refreshToken)) {
            refreshToken = jwtUtil.generateRefreshToken(user);
            tokenRedisService.saveRefreshToken(user.getUserId(), refreshToken);
        }

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
