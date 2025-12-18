package com.sooscode.sooscode_api.global.guard;

import com.sooscode.sooscode_api.global.exception.CustomException;
import com.sooscode.sooscode_api.global.status.CompileStatus;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 컴파일 요청 쿨타임 가드
 *
 * 같은 사용자가 짧은 시간 안에
 * 컴파일 요청을 여러 번 보내는 것을 막는다.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class RequestCooldownGuard {

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(cooldown)")
    public Object checkCooldown(
            ProceedingJoinPoint joinPoint,
            RequestCooldown cooldown
    ) throws Throwable {

        // 인증된 사용자 식별자
        String userId = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // Redis 쿨타임 키
        String cooldownKey = "compile:cooldown:" + userId;

        // 쿨타임 등록 시도 (이미 있으면 false)
        Boolean allowed = redisTemplate.opsForValue()
                .setIfAbsent(
                        cooldownKey,
                        "1",
                        cooldown.seconds(),
                        TimeUnit.SECONDS
                );

        // 이미 쿨타임이면 요청 차단
        if (Boolean.FALSE.equals(allowed)) {
            throw new CustomException(CompileStatus.TOO_MANY_REQUESTS);
        }

        // 정상 요청 진행
        return joinPoint.proceed();
    }
}
