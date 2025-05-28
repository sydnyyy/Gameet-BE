package com.gameet.match.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

public record MatchAppointmentRequest (

        @NotNull(message = "매칭룸 아이디는 필수입니다.")
        long matchRoomId,

        @NotNull(message = "약속 시간은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
        @DateTimeFormat(pattern = "HH:mm")
        LocalTime matchAppointmentTime
) {
}
