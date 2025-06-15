package com.gameet.match.dto.request;

import com.gameet.match.enums.MannerEvaluation;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MatchMannerEvaluationRequest(

        @NotNull(message = "매칭 방 아이디는 필수입니다.")
        Long matchRoomId,

        @NotNull(message = "매너 평가는 필수입니다.")
        MannerEvaluation mannerEvaluation

) {
}
