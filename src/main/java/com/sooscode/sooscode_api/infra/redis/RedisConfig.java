package com.sooscode.sooscode_api.infra.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 서버 연결 정보 세팅하는 곳
        template.setConnectionFactory(connectionFactory);
        // 키를 문자열로 저장, 이거 안해주면 바이트 배열로 저장되어 redis-cil에서 볼 때 깨짐
        template.setKeySerializer(new StringRedisSerializer());
        // 값을 JSON 형태로 저장, 객체 저장할 때 자동으로 직렬화/역직렬화 해줌
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // Redis 자료구조 설정, Hash 필드명을 문자열로 값을 JSON으로
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}