package com.sooscode.sooscode_api.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenRedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "token:refresh:";
    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    private static final long REFRESH_TOKEN_TTL_DAYS = 7;

    /**
     * Refresh Token 저장
     */
    public void saveRefreshToken(Long userId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_TTL_DAYS, TimeUnit.DAYS);
    }

    /**
     * Refresh Token 조회
     */
    public String getRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Refresh Token 삭제 (로그아웃 시)
     */
    public void deleteRefreshToken(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
    }

    /**
     * Access Token 블랙리스트 등록 (로그아웃 시)
     * @param accessToken 블랙리스트에 등록할 토큰
     * @param remainingMillis 토큰 남은 만료 시간 (밀리초)
     */
    public void addToBlacklist(String accessToken, long remainingMillis) {
        String key = BLACKLIST_PREFIX + accessToken;
        redisTemplate.opsForValue().set(key, "true", remainingMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Access Token 블랙리스트 여부 확인
     */
    public boolean isBlacklisted(String accessToken) {
        String key = BLACKLIST_PREFIX + accessToken;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}