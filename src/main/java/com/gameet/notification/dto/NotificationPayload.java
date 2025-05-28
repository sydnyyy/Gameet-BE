package com.gameet.notification.dto;

import com.gameet.match.enums.MatchStatus;
import com.gameet.notification.enums.MessageType;
import lombok.Builder;

@Builder
public record NotificationPayload (
        MessageType messageType,
        MatchStatus matchStatus,
        String content
) {

    public static NotificationPayload of(MessageType messageType, MatchStatus matchStatus) {
        return NotificationPayload.builder()
                .messageType(messageType)
                .matchStatus(matchStatus)
                .content(MatchStatus.getMessage(matchStatus))
                .build();
    }
}
