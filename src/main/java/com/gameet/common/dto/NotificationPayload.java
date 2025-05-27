package com.gameet.common.dto;

import com.gameet.match.enums.MatchStatus;
import lombok.Builder;

@Builder
public record NotificationPayload (
        MatchStatus status,
        String content
) {

    public static NotificationPayload of(MatchStatus matchStatus) {
        return NotificationPayload.builder()
                .status(matchStatus)
                .content(MatchStatus.getMessage(matchStatus))
                .build();
    }
}
