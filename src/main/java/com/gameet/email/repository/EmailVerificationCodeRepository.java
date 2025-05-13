package com.gameet.email.repository;

import com.gameet.email.enums.EmailPurpose;
import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class EmailVerificationCodeRepository {

    private final String SIGN_UP_EMAIL_VERIFICATION_CODE_PREFIX = "sign_up_email_verification_code:";
    private final String PASSWORD_RESET_EMAIL_VERIFICATION_CODE_PREFIX = "password_reset_email_verification_code:";

    private final RedisTemplate<String, String> redisTemplate;

    public void saveEmailVerificationCode(String email, String verificationCode, EmailPurpose emailPurpose) {
        String key = getKey(email, emailPurpose);
        redisTemplate.opsForValue()
                .set(key, verificationCode, Duration.ofMinutes(5));
    }

    public Boolean isValidEmailVerificationCode(String email, String requestedCode, EmailPurpose emailPurpose) {
        String key = getKey(email, emailPurpose);
        String verificationCode = redisTemplate.opsForValue().get(key);
        return verificationCode != null && verificationCode.equals(requestedCode);
    }

    public void deleteEmailVerificationCode(String email, EmailPurpose emailPurpose) {
        String key = getKey(email, emailPurpose);
        redisTemplate.delete(key);
    }

    private String getKey(String email, EmailPurpose emailPurpose) {
        switch (emailPurpose) {
            case EmailPurpose.SIGN_UP -> {
                return SIGN_UP_EMAIL_VERIFICATION_CODE_PREFIX + email;
            }
            case EmailPurpose.PASSWORD_RESET -> {
                return PASSWORD_RESET_EMAIL_VERIFICATION_CODE_PREFIX + email;
            }
        }
        throw new CustomException(ErrorCode.INVALID_EMAIL_PURPOSE);
    }
}
