package com.gameet.match.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gameet.match.entity.MatchAppointment;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Builder
public record MatchAppointmentResponse (

        long matchRoomId,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        @DateTimeFormat(pattern = "HH:mm")
        LocalTime matchAppointmentTime
) {

    public static MatchAppointmentResponse of(MatchAppointment matchAppointment) {
        return MatchAppointmentResponse.builder()
                .matchRoomId(matchAppointment.getMatchRoomId())
                .matchAppointmentTime(matchAppointment.getAppointmentAt().toLocalTime())
                .build();
    }
}
