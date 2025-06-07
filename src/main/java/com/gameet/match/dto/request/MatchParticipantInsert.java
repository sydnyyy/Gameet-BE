package com.gameet.match.dto.request;

import com.gameet.user.entity.UserProfile;
import lombok.Builder;

@Builder
public record MatchParticipantInsert(
        UserProfile userProfile,
        MatchSuccessConditionInsert condition
) {
}
