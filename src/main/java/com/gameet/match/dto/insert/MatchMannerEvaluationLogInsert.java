package com.gameet.match.dto.insert;

import com.gameet.match.entity.MatchParticipant;
import com.gameet.match.enums.MannerEvaluation;
import lombok.Builder;

@Builder
public record MatchMannerEvaluationLogInsert(
        MatchParticipant matchParticipant,
        MannerEvaluation mannerEvaluation
) {
}
