package com.gameet.match.mapper;

import com.gameet.match.domain.MatchCondition;
import com.gameet.match.dto.request.MatchConditionRequest;
import com.gameet.user.entity.UserProfile;

public class MatchConditionMapper {
    public static MatchCondition from(UserProfile userProfile, MatchConditionRequest matchConditionRequest) {
        return MatchCondition.builder()
                .preferredGenres(matchConditionRequest.preferredGenres())
                .gamePlatforms(matchConditionRequest.gamePlatforms())
                .playStyle(matchConditionRequest.playStyle())
                .gameSkillLevel(matchConditionRequest.gameSkillLevel())
                .isAdultMatchAllowed(matchConditionRequest.isAdultMatchAllowed())
                .age(userProfile.getAge())
                .isVoice(matchConditionRequest.isVoice())
                .minMannerScore(matchConditionRequest.minMannerScore())
                .mannerScore(userProfile.getMannerScore())
                .build();
    }
}
