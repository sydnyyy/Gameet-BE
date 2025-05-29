package com.gameet.notification.dto;

import com.gameet.match.entity.MatchAppointment;
import com.gameet.match.enums.MatchStatus;
import com.gameet.notification.enums.MessageType;
import lombok.Builder;

@Builder
public record NotificationPayload (
        MessageType messageType,
        MatchStatus matchStatus,
        Long matchRoomId,
        String content
) {

    public static NotificationPayload fromMatchResult(MatchStatus matchStatus, Long matchRoomId) {
        return NotificationPayload.builder()
                .messageType(MessageType.MATCH_RESULT)
                .matchStatus(matchStatus)
                .matchRoomId(matchRoomId)
                .content(MatchStatus.getMessage(matchStatus))
                .build();
    }

    public static NotificationPayload fromMatchAppointment(MatchAppointment matchAppointment) {
        return NotificationPayload.builder()
                .messageType(MessageType.MATCH_APPOINTMENT)
                .matchStatus(MatchStatus.MATCHED)
                .matchRoomId(matchAppointment.getMatchRoomId())
                .content(matchAppointment.getAppointmentAt().toLocalTime() + " 예약 시간이 다가옵니다!")
                .build();
    }
}
