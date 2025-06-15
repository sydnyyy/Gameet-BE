package com.gameet.match.entity;

import com.gameet.match.enums.ReportReason;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchReportLogId {

    @Column(name = "match_participant_id")
    private Long matchParticipantId;

    @Column(name = "report_reason")
    @Enumerated(EnumType.STRING)
    private ReportReason reportReason;

    public static MatchReportLogId of(ReportReason reportReason) {
        return MatchReportLogId.builder()
                .reportReason(reportReason)
                .build();
    }
}
