package com.gameet.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotifier {

    private final JavaMailSender javaMailSender;

    public void send(String toEmail, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[Gameet] " + subject);
        message.setText(content);

        javaMailSender.send(message);
    }

    @Async("defaultEmailExecutor")
    public void sendAsync(String toEmail, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[Gameet] " + subject);
        message.setText(content);

        javaMailSender.send(message);
    }
}
