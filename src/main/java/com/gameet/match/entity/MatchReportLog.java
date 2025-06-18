package com.gameet.match.entity;

import com.gameet.common.entity.BaseTimeEntity;
import com.gameet.match.dto.insert.MatchReportLogInsert;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "match_report_log")
public class MatchReportLog extends BaseTimeEntity {

    @EmbeddedId
    private MatchReportLogId id;

    @MapsId("matchParticipantId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_participant_id")
    private MatchParticipant matchParticipant;

    public static MatchReportLog of(MatchReportLogInsert insert) {
        return MatchReportLog.builder()
                .id(MatchReportLogId.of(insert.reportReason()))
                .matchParticipant(insert.matchParticipant())
                .build();
    }
}

