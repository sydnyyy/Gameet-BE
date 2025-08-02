package com.gameet.notification.service;

import com.gameet.common.service.EmailNotifier;
import com.gameet.match.entity.MatchAppointment;
import com.gameet.match.repository.MatchParticipantRepository;
import com.gameet.notification.dto.NotificationPayload;
import com.gameet.match.enums.MatchStatus;
import com.gameet.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final EmailNotifier emailNotifier;
    private final MatchParticipantRepository matchParticipantRepository;
    private final UserRepository userRepository;

    public void sendMatchResult(List<Long> userId, MatchStatus matchStatus, Long matchRoomId) {
        userId.forEach(id -> sendMatchResult(id, matchStatus, matchRoomId));
    }

    public void sendMatchResult(Long userId, MatchStatus matchStatus, Long matchRoomId) {
        NotificationPayload payload = NotificationPayload.fromMatchResult(matchStatus, matchRoomId);
        sendWebNotification(userId, payload);

        Optional<String> toEmail = userRepository.findEmailByUserId(userId);
        if (toEmail.isEmpty()) {
            log.error("[sendMatchResult] userId={} 의 이메일 존재하지 않음", userId);
            return;
        }

        String subject = "매칭 결과 알림입니다.";
        String content = MatchStatus.getMessage(matchStatus) + "\n페이지에 접속해주세요!";
        emailNotifier.sendAsync(toEmail.get(), subject, content);
    }

    @Async
    public void sendMatchAppointmentAsync(List<MatchAppointment> matchAppointments) {
        matchAppointments.forEach(this::sendMatchAppointment);
    }

    private void sendMatchAppointment(MatchAppointment matchAppointment) {
        NotificationPayload payload = NotificationPayload.fromMatchAppointment(matchAppointment);
        List<Long> userId = matchParticipantRepository.findUserIdsByMatchRoomId(matchAppointment.getMatchRoomId());
        userId.forEach(id -> {
            sendWebNotification(id, payload);

            Optional<String> toEmail = userRepository.findEmailByUserId(id);
            if (toEmail.isEmpty()) {
                log.error("[sendMatchResult] userId={} 의 이메일 존재하지 않음", id);
                return;
            }

            String subject = payload.content();
            String content = subject + "\n게임에 접속해주세요!";
            emailNotifier.sendAsync(toEmail.get(), subject, content);
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
