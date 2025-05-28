package com.gameet.notification.service;

import com.gameet.notification.dto.NotificationPayload;
import com.gameet.match.enums.MatchStatus;
import com.gameet.notification.enums.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final EmailService emailService;

    public void sendMatchResult(MessageType messageType, List<Long> userId, MatchStatus matchStatus, Long matchRoomId) {
        userId.forEach(id -> sendMatchResult(messageType, id, matchStatus, matchRoomId));
    }

    public void sendMatchResult(MessageType messageType, Long userId, MatchStatus matchStatus, Long matchRoomId) {
        sendWebNotification(messageType, userId, matchStatus, matchRoomId);
        emailService.sendMatchResultAsync(userId, matchStatus);
    }

    private void sendWebNotification(MessageType messageType, Long userId, MatchStatus matchStatus, Long matchRoomId) {
        NotificationPayload payload = NotificationPayload.of(messageType, matchStatus, matchRoomId);

        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/notify",
                payload
        );
    }
}
