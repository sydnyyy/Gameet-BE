package com.gameet.match.dto.insert;

import com.gameet.match.entity.MatchParticipant;
import com.gameet.match.enums.ReportReason;
import lombok.Builder;

@Builder
public record MatchReportLogInsert(
        MatchParticipant matchParticipant,
        ReportReason reportReason
) {
}
