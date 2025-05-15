package com.gameet.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

    private final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + userId, refreshToken, Duration.ofDays(7));
    }

    public void deleteRefreshTokenByUserId(Long userId) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }
}
