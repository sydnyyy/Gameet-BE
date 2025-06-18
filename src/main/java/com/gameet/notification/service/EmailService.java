package com.gameet.notification.service;

import com.gameet.common.enums.EmailPurpose;
import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.match.enums.MatchStatus;
import com.gameet.notification.entity.MatchEmailSendLog;
import com.gameet.notification.enums.MessageType;
import com.gameet.notification.repository.MatchEmailSendLogRepository;
import com.gameet.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final MatchEmailSendLogRepository matchEmailSendLogRepository;

    public void sendVerificationCode(String toEmail, String verificationCode, EmailPurpose emailPurpose) {
        String subject = emailPurpose.getDescription() + " 이메일 인증 코드입니다.";
        String content = "인증 코드: " + verificationCode + "\n\n" +
                "인증 코드 입력 기한은 5분입니다.\n" +
                "요청하지 않았다면 메일을 무시하셔도 됩니다.\n";

        send(toEmail, subject, content);
    }

    @Async("emailExecutor")
    public void sendMatchResultAsync(Long userId, MatchStatus matchStatus) {
        String toEmail = userRepository.findEmailByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EMAIL));

        String subject = MatchStatus.getMessage(matchStatus);
        String content = subject + "\n페이지에 접속해주세요!";
        send(toEmail, subject, subject);

        MatchEmailSendLog sendLog = MatchEmailSendLog.of(userId, MessageType.MATCH_RESULT, content, LocalDateTime.now());
        matchEmailSendLogRepository.save(sendLog);
    }

    @Async("emailExecutor")
    public void sendMatchAppointmentAsync(Long userId, String subject) {
        String toEmail = userRepository.findEmailByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EMAIL));

        String content = subject + "\n게임에 접속해주세요!";
        send(toEmail, subject, content);

        MatchEmailSendLog sendLog = MatchEmailSendLog.of(userId, MessageType.MATCH_RESULT, content, LocalDateTime.now());
        matchEmailSendLogRepository.save(sendLog);
    }

    private void send(String toEmail, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[Gameet] " + subject);
        message.setText(content);

        javaMailSender.send(message);
    }
}
