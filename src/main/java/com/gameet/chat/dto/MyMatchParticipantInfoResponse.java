package com.gameet.chat.dto;

import com.gameet.match.entity.MatchParticipant;
import lombok.Builder;
import org.springframework.util.CollectionUtils;

@Builder
public record MyMatchParticipantInfoResponse (
        Long matchParticipantId,
        Boolean isReported
) {
    public static MyMatchParticipantInfoResponse of(MatchParticipant matchParticipant) {
        return MyMatchParticipantInfoResponse.builder()
                .matchParticipantId(matchParticipant.getMatchParticipantId())
                .isReported(!CollectionUtils.isEmpty(matchParticipant.getMatchReportLog()))
                .build();
    }
}
