package com.gameet.notification.service;

import com.gameet.common.service.EmailNotifier;
import com.gameet.global.exception.CriticalDataException;
import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.match.dto.response.ParticipantInfoDto;
import com.gameet.match.entity.MatchAppointment;
import com.gameet.match.repository.MatchParticipantRepository;
import com.gameet.notification.dto.NotificationPayload;
import com.gameet.notification.enums.EmailSendingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentProcessor {

    private final MatchParticipantRepository matchParticipantRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final EmailNotifier emailNotifier;

    @Transactional
    public void notifyParticipantsOfAppointment(MatchAppointment matchAppointment) {
        NotificationPayload payload = NotificationPayload.fromMatchAppointment(matchAppointment);

        List<Long> successUserIds = new ArrayList<>();
        Map<Long, Exception> failedUsers = new HashMap<>();

        List<ParticipantInfoDto> participantInfoDtos = matchParticipantRepository.findParticipantInfoByMatchRoomId(matchAppointment.getMatchRoomId());
        participantInfoDtos.forEach(participantInfoDto -> {
            try {
                sendWebNotification(participantInfoDto.userId(), payload);

                if (!StringUtils.hasText(participantInfoDto.email())) {
                    log.error("[notifyParticipantsOfAppointment] userId=" + participantInfoDto.userId() + " 사용자의 이메일 존재하지 않음");
                    throw new CriticalDataException(ErrorCode.NOT_FOUND_EMAIL.getMessage(), List.of(participantInfoDto.userId()));
                }
                String subject = payload.content();
                String content = subject + "\n게임에 접속해주세요.";
                emailNotifier.send(participantInfoDto.email(), subject, content);

                successUserIds.add(participantInfoDto.userId());
            } catch (Exception e) {
                failedUsers.put(participantInfoDto.userId(), e);
            }
        });

        if (!successUserIds.isEmpty()) {
            matchParticipantRepository.updateStatusByUserProfileIds(successUserIds, EmailSendingStatus.SENT);
        }
        if (!failedUsers.isEmpty()) {
            matchParticipantRepository.updateStatusByUserProfileIds(new ArrayList<>(failedUsers.keySet()), EmailSendingStatus.FAILED);

            List<Long> criticalFailureUserIds = failedUsers.entrySet().stream()
                    .filter(entry -> entry.getValue() instanceof CriticalDataException)
                    .map(Map.Entry::getKey)
                    .toList();

            if (!criticalFailureUserIds.isEmpty()) {
                throw new CriticalDataException(
                        "matchRoomId=" + matchAppointment.getMatchRoomId() + " 에 대한 예약 알림 이메일 전송 로직에서 이메일이 존재하지 않는 유저 발견",
                        criticalFailureUserIds
                );
            } else {
                throw new CustomException(ErrorCode.EMAIL_SEND_FAIL);
            }
        }
    }

    private void sendWebNotification(Long userId, NotificationPayload payload) {
        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/notify",
                payload
        );
    }
}
