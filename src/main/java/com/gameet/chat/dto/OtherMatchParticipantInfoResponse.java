package com.gameet.chat.dto;

import com.gameet.match.entity.MatchParticipant;
import com.gameet.user.dto.response.UserPublicProfileResponse;
import lombok.Builder;

@Builder
public record OtherMatchParticipantInfoResponse (
        Long matchParticipantId,
        UserPublicProfileResponse userProfile
){
    public static OtherMatchParticipantInfoResponse of(MatchParticipant matchParticipant) {
        return OtherMatchParticipantInfoResponse.builder()
                .matchParticipantId(matchParticipant.getMatchParticipantId())
                .userProfile(UserPublicProfileResponse.of(matchParticipant.getUserProfile()))
                .build();
    }
}
