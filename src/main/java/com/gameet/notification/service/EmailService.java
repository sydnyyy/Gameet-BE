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

    @Async("emailExecutor")
    public void sendMatchResultAsync(Long userId, MatchStatus matchStatus) {
        String toEmail = userRepository.findEmailByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_EMAIL));

        String content = MatchStatus.getMessage(matchStatus);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[Gameet] " + content);
        message.setText(content + "\n페이지에 접속해주세요!");

        javaMailSender.send(message);

        MatchEmailSendLog sendLog = MatchEmailSendLog.of(userId, MessageType.MATCH_RESULT, content, LocalDateTime.now());
        matchEmailSendLogRepository.save(sendLog);
    }
}
