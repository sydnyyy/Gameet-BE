package com.gameet.notification.dto;

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

    public static NotificationPayload of(MessageType messageType, MatchStatus matchStatus, Long matchRoomId) {
        return NotificationPayload.builder()
                .messageType(messageType)
                .matchStatus(matchStatus)
                .matchRoomId(matchRoomId)
                .content(MatchStatus.getMessage(matchStatus))
                .build();
    }
}
