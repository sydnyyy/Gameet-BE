package com.gameet.common.service;

import com.gameet.common.enums.EmailPurpose;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendVerificationCode(String toEmail, String verificationCode, EmailPurpose emailPurpose) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[Gameet] " + emailPurpose.getDescription() + " 이메일 인증 코드입니다.");
        message.setText("Gameet " + emailPurpose.getDescription() + " 이메일 인증 코드입니다.\n\n" +
                "인증 코드: " + verificationCode + "\n\n" +
                "인증 코드 입력 기한은 5분입니다.\n" +
                "요청하지 않았다면 메일을 무시하셔도 됩니다.\n" +
                "감사합니다.");

        javaMailSender.send(message);
    }
}
