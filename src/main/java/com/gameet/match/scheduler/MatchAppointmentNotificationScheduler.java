package com.gameet.match.scheduler;

import com.gameet.match.entity.MatchAppointment;
import com.gameet.match.repository.MatchAppointmentRepository;
import com.gameet.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MatchAppointmentNotificationScheduler {

    private final MatchAppointmentRepository matchAppointmentRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 * * * * *")
    public void schedule() {
        LocalTime targetTime = LocalTime.now().plusMinutes(10).withSecond(0).withNano(0);
        LocalDateTime targetDateTime = LocalDateTime.of(LocalDate.now(), targetTime);

        List<MatchAppointment> matchAppointments = matchAppointmentRepository.findAllByAppointmentAt(targetDateTime);
        if (matchAppointments != null && !matchAppointments.isEmpty()) {
            notificationService.sendMatchAppointmentAsync(matchAppointments);
        }
    }
}
