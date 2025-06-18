package com.gameet.match.entity;

import com.gameet.match.dto.request.MatchAppointmentRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchAppointmentId;

    @Column(nullable = false)
    private Long matchRoomId;

    @Column(nullable = false)
    private LocalDateTime appointmentAt;

    public static MatchAppointment of(MatchAppointmentRequest matchAppointmentRequest) {
        return MatchAppointment.builder()
                .matchRoomId(matchAppointmentRequest.matchRoomId())
                .appointmentAt(LocalDateTime.of(LocalDate.now(), matchAppointmentRequest.matchAppointmentTime()))
                .build();
    }
}
