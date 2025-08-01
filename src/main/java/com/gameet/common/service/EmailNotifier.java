package com.gameet.common.service;

import com.gameet.common.enums.EmailPurpose;
import com.gameet.match.enums.MatchStatus;
import com.gameet.notification.entity.MatchEmailSendLog;
import com.gameet.notification.enums.MessageType;
import com.gameet.notification.repository.MatchEmailSendLogRepository;
import com.gameet.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotifier {

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
        Optional<String> toEmail = userRepository.findEmailByUserId(userId);
        if (toEmail.isEmpty()) {
            log.error("이메일 발송 실패: userId '{}'에 해당하는 사용자의 이메일을 찾을 수 없음", userId);
            return;
        }

        String subject = MatchStatus.getMessage(matchStatus);
        String content = subject + "\n페이지에 접속해주세요!";
        send(toEmail.get(), subject, subject);

        MatchEmailSendLog sendLog = MatchEmailSendLog.of(userId, MessageType.MATCH_RESULT, content, LocalDateTime.now());
        matchEmailSendLogRepository.save(sendLog);
    }

    @Async("emailExecutor")
    public void sendMatchAppointmentAsync(Long userId, String subject) {
        Optional<String> toEmail = userRepository.findEmailByUserId(userId);
        if (toEmail.isEmpty()) {
            log.error("이메일 발송 실패: userId '{}'에 해당하는 사용자의 이메일을 찾을 수 없음", userId);
            return;
        }

        String content = subject + "\n게임에 접속해주세요!";
        send(toEmail.get(), subject, content);

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
