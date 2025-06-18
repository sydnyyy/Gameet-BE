package com.gameet.match.dto.insert;

import com.gameet.user.entity.UserProfile;
import lombok.Builder;

@Builder
public record MatchParticipantInsert(
        UserProfile userProfile,
        MatchSuccessConditionInsert condition
) {
}
