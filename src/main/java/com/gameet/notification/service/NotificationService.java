package com.gameet.notification.service;

import com.gameet.common.enums.AlertLevel;
import com.gameet.common.service.DiscordNotifier;
import com.gameet.global.exception.CriticalDataException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.match.entity.MatchAppointment;
import com.gameet.notification.dto.TemplatedEmailRequest;
import com.gameet.notification.dto.response.WebSocketPayload;
import com.gameet.match.enums.MatchStatus;
import com.gameet.notification.enums.AwsSesTemplateType;
import com.gameet.user.repository.UserRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Resource(name = "appointmentNotificationExecutor")
    private Executor appointmentNotificationExecutor;

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AwsSesEmailNotifier emailNotifier;
    private final UserRepository userRepository;
    private final AppointmentProcessor appointmentProcessor;
    private final DiscordNotifier discordNotifier;

    public void sendMatchResult(List<Long> userId, MatchStatus matchStatus, Long matchRoomId) {
        userId.forEach(id -> sendMatchResult(id, matchStatus, matchRoomId));
    }

    public void sendMatchResult(Long userId, MatchStatus matchStatus, Long matchRoomId) {
        // 웹 알림
        WebSocketPayload payload = WebSocketPayload.fromMatchResult(matchStatus, matchRoomId);
        sendWebNotification(userId, payload);

        // 이메일 알림
        sendEmailNotification(userId, matchStatus);
    }

    private void sendWebNotification(Long userId, WebSocketPayload payload) {
        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/notify",
                payload
        );
    }

    private void sendEmailNotification(Long userId, MatchStatus matchStatus) {
        Optional<String> toEmail = userRepository.findEmailByUserId(userId);
        if (toEmail.isEmpty()) {
            log.error("[sendMatchResult] userId={} 의 이메일 존재하지 않음", userId);
            throw new CriticalDataException(ErrorCode.NOT_FOUND_EMAIL.getMessage(), List.of(userId));
        }

        TemplatedEmailRequest emailRequest = TemplatedEmailRequest.builder()
                .toEmail(toEmail.get())
                .awsSesTemplateType(AwsSesTemplateType.MATCH_RESULT)
                .templateData(Map.of(
                        "matchResult", MatchStatus.getMessage(matchStatus)))
                .build();

        emailNotifier.sendTemplatedEmail(emailRequest);
    }

    @Async("appointmentNotificationExecutor")
    public void notifyAllAppointmentsAsync(List<MatchAppointment> matchAppointments) {
        if (matchAppointments == null || matchAppointments.isEmpty()) {
            return;
        }

        final Map<Long, Throwable> criticalFailures = new ConcurrentHashMap<>();

        List<CompletableFuture<Void>> futures = matchAppointments.stream()
                .map(appointment -> CompletableFuture.runAsync(() -> {
                    appointmentProcessor.notifyParticipantsOfAppointment(appointment);
                    }, appointmentNotificationExecutor)
                        .exceptionally(ex -> {
                            Throwable cause = ex.getCause();
                            log.error("matchRoomId={} 에 대한 비동기 예약 알림 처리 중 예외 발생", appointment.getMatchRoomId(), cause);

                            if (cause instanceof CriticalDataException) {
                                criticalFailures.put(appointment.getMatchRoomId(), cause);
                            }
                            return null;
                        }))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        if (!criticalFailures.isEmpty()) {
            String title = "🔴[데이터 정합성 오류] 예약 알림 처리 중 데이터 정합성 오류 감지";
            String summaryMessage = createDiscordSummaryMessage(criticalFailures);
            discordNotifier.send(title, summaryMessage, AlertLevel.CRITICAL);
        }
    }

    private String createDiscordSummaryMessage(Map<Long, Throwable> failures) {
        StringBuilder message = new StringBuilder();
        failures.forEach((roomId, exception) -> {
            message.append(String.format("- Room ID: %d\n", roomId));
            if (exception instanceof CriticalDataException cdEx) {
                message.append(String.format("  - 문제 사용자 ID: `%s`\n", cdEx.getUserIds()));
                message.append(String.format("  - 원인: %s\n", exception.getMessage()));
                message.append("---------------------");
            }
        });
        return message.toString();
    }

    public void sendChatNotification(Long receiverUserId, Long matchRoomId, Long senderId) {
        WebSocketPayload payload = WebSocketPayload.fromMatchChat(matchRoomId, senderId);
        sendWebNotification(receiverUserId, payload);
    }
}
