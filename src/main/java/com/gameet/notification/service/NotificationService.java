package com.gameet.notification.service;

import com.gameet.notification.dto.NotificationPayload;
import com.gameet.match.enums.MatchStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final EmailService emailService;

    public void sendMatchResult(List<Long> userId, MatchStatus matchStatus) {
        userId.forEach(id -> sendMatchResult(id, matchStatus));
    }

    public void sendMatchResult(Long userId, MatchStatus matchStatus) {
        sendWebNotification(userId, matchStatus);
        emailService.sendMatchResultAsync(userId, matchStatus);
    }

    private void sendWebNotification(Long userId, MatchStatus matchStatus) {
        NotificationPayload payload = NotificationPayload.of(matchStatus);

        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/notify",
                payload
        );
    }
}
