package com.gameet.notification.service;

import com.gameet.common.service.EmailNotifier;
import com.gameet.match.entity.MatchAppointment;
import com.gameet.match.repository.MatchParticipantRepository;
import com.gameet.notification.dto.NotificationPayload;
import com.gameet.match.enums.MatchStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final EmailNotifier emailNotifier;
    private final MatchParticipantRepository matchParticipantRepository;

    public void sendMatchResult(List<Long> userId, MatchStatus matchStatus, Long matchRoomId) {
        userId.forEach(id -> sendMatchResult(id, matchStatus, matchRoomId));
    }

    public void sendMatchResult(Long userId, MatchStatus matchStatus, Long matchRoomId) {
        NotificationPayload payload = NotificationPayload.fromMatchResult(matchStatus, matchRoomId);
        sendWebNotification(userId, payload);

        emailNotifier.sendMatchResultAsync(userId, matchStatus);
    }

    @Async
    public void sendMatchAppointmentAsync(List<MatchAppointment> matchAppointments) {
        matchAppointments.forEach(this::sendMatchAppointment);
    }

    private void sendMatchAppointment(MatchAppointment matchAppointment) {
        NotificationPayload payload = NotificationPayload.fromMatchAppointment(matchAppointment);
        List<Long> userId = matchParticipantRepository.findUserIdsByMatchRoomId(matchAppointment.getMatchRoomId());
        userId.forEach(id -> {
                    emailNotifier.sendMatchAppointmentAsync(id, payload.content());
                    sendWebNotification(id, payload);
                });
    }

    public void sendChatNotification(Long receiverUserId, Long matchRoomId, Long senderId) {
        NotificationPayload payload = NotificationPayload.fromMatchChat(matchRoomId, senderId);
        sendWebNotification(receiverUserId, payload);
    }

    private void sendWebNotification(Long userId, NotificationPayload payload) {
        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/notify",
                payload
        );
    }
}
