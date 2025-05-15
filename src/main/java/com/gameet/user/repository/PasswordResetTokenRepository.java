package com.gameet.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class PasswordResetTokenRepository {

    private final String PASSWORD_RESET_TOKEN_PREFIX = "password_reset_token:";
    private final RedisTemplate<String, String> redisTemplate;

    public String issuePasswordResetToken(String email) {
        String token = generateRandomToken();
        redisTemplate.opsForValue().set(PASSWORD_RESET_TOKEN_PREFIX + email, token, Duration.ofMinutes(3));
        return token;
    }

    private static String generateRandomToken() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(128, random).toString(32);
    }

    public Boolean isPasswordResetTokenValid(String email, String requestedToken) {
        String token = redisTemplate.opsForValue().get(PASSWORD_RESET_TOKEN_PREFIX + email);
        return token != null && token.equals(requestedToken);
    }

    public void deletePasswordResetToken(String email) {
        redisTemplate.delete(PASSWORD_RESET_TOKEN_PREFIX + email);
    }
}
