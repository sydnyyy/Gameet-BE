package com.gameet.match.dto.insert;

import com.gameet.match.enums.MatchStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record MatchRoomInsert(
        MatchStatus matchStatus,
        List<MatchParticipantInsert> participants
) {
}
