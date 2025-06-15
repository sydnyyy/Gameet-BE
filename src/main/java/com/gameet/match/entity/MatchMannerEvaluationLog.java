package com.gameet.match.entity;

import com.gameet.common.entity.BaseTimeEntity;
import com.gameet.match.dto.insert.MatchMannerEvaluationLogInsert;
import com.gameet.match.enums.MannerEvaluation;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "match_manner_evaluation_log")
public class MatchMannerEvaluationLog extends BaseTimeEntity {

    @Id
    @Column(name = "match_participant_id")
    private Long matchParticipantId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_participant_id")
    @Setter(AccessLevel.PACKAGE)
    private MatchParticipant matchParticipant;

    @Column(name = "manner_evaluation")
    @Enumerated(EnumType.STRING)
    private MannerEvaluation mannerEvaluation;

    public static MatchMannerEvaluationLog of(MatchMannerEvaluationLogInsert insert) {
        return MatchMannerEvaluationLog.builder()
                .matchParticipant(insert.matchParticipant())
                .mannerEvaluation(insert.mannerEvaluation())
                .build();
    }
}

