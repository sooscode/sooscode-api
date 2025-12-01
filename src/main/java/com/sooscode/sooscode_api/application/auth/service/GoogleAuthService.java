package com.sooscode.sooscode_api.application.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.sooscode.sooscode_api.application.auth.dto.*;
import com.sooscode.sooscode_api.global.config.GoogleOAuthConfig;
import com.sooscode.sooscode_api.global.jwt.JwtUtil;
import com.sooscode.sooscode_api.application.user.entity.User;
import com.sooscode.sooscode_api.domain.user.enums.UserRole;
import com.sooscode.sooscode_api.domain.user.enums.UserStatus;
import com.sooscode.sooscode_api.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final GoogleOAuthConfig googleOAuthConfig;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // Google 로그인 URL 생성
    public String buildGoogleLoginUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth" +
                "?client_id=" + googleOAuthConfig.getClientId() +
                "&redirect_uri=" + googleOAuthConfig.getRedirectUri() +
                "&response_type=code" +
                "&scope=openid%20email%20profile" +
                "&access_type=offline";
    }

    // Google Callback 처리 (AccessToken + RefreshToken 발급)
    public LoginResponse processGoogleCallback(String code) {

        GoogleOAuthToken tokenResponse = getAccessToken(code);
        GoogleUserInfo userInfo = getUserInfo(tokenResponse.access_token());

        String email = userInfo.email();

        // 기존유저 or 신규유저 생성
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(createGoogleUser(userInfo)));

        // jwt 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return new LoginResponse(accessToken, refreshToken);
    }

    // code → oauth token
    private GoogleOAuthToken getAccessToken(String code) {

        String url = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleOAuthConfig.getClientId());
        params.add("client_secret", googleOAuthConfig.getClientSecret());
        params.add("redirect_uri", googleOAuthConfig.getRedirectUri());
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> request = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(url, request, GoogleOAuthToken.class);
    }

    // access-token → user info
    private GoogleUserInfo getUserInfo(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, request, GoogleUserInfo.class)
                .getBody();
    }

    // 신규 유저 생성
    private User createGoogleUser(GoogleUserInfo info) {
        User newUser = new User();
        newUser.setEmail(info.email());
        newUser.setPassword("GOOGLE_USER");
        newUser.setName(info.name());
        newUser.setProvider("google");
        newUser.setRole(UserRole.STUDENT);
        newUser.setStatus(UserStatus.ACTIVE);
        return newUser;
    }
}
