package com.gameet.match.dto.response;

import com.gameet.match.enums.MatchStatus;
import lombok.Builder;

@Builder
public record MatchStatusWithInfoResponse(
        MatchStatus matchStatus,
        Long elapsedTime,
        Long matchRoomId
) {
}
