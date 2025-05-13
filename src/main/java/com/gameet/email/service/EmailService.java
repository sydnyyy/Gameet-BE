package com.gameet.email.service;

import com.gameet.email.enums.EmailPurpose;
import com.gameet.email.repository.EmailVerificationCodeRepository;
import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final UserService userService;

    public void sendSignupCode(String toEmail, EmailPurpose emailPurpose) {
        if (emailPurpose == EmailPurpose.PASSWORD_RESET) {
            if (!userService.isExistUserByEmail(toEmail)) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND_BY_EMAIL);
            }
        }

        String verificationCode = generateRandomCode();

        try {
            saveVerificationCode(toEmail, verificationCode, emailPurpose);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("[Gameet] " + emailPurpose.getDescription() + " 이메일 인증 코드입니다.");
            message.setText("Gameet " + emailPurpose.getDescription() + " 이메일 인증 코드입니다.\n\n" +
                    "인증 코드: " + verificationCode + "\n\n" +
                    "인증 코드 입력 기한은 5분입니다.\n" +
                    "요청하지 않았다면 메일을 무시하셔도 됩니다.\n" +
                    "감사합니다.");

            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("이메일 인증 코드 처리 실패", e);
            throw new CustomException(ErrorCode.EMAIL_SEND_FAIL);
        }
    }

    public void verifyEmailCode(String email, String code, EmailPurpose emailPurpose) {
        Boolean isValid = emailVerificationCodeRepository.isValidEmailVerificationCode(email, code, emailPurpose);
        if (!isValid) {
            throw new CustomException(ErrorCode.EMAIL_VERIFICATION_FAILED);
        }
        else {
            emailVerificationCodeRepository.deleteEmailVerificationCode(email, emailPurpose);
        }
    }

    private String generateRandomCode() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    private void saveVerificationCode(String email, String code, EmailPurpose emailPurpose) {
        emailVerificationCodeRepository.saveEmailVerificationCode(email, code, emailPurpose);
    }
}
