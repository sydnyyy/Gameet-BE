package com.gameet.match.mapper;

import com.gameet.match.domain.MatchCondition;
import com.gameet.match.dto.request.MatchConditionRequest;
import com.gameet.match.dto.request.MatchParticipantInsert;
import com.gameet.match.dto.request.MatchSuccessConditionInsert;
import com.gameet.user.entity.UserProfile;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MatchMapper {
    public static MatchCondition toMatchCondition(UserProfile userProfile, MatchConditionRequest matchConditionRequest) {
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

    public static MatchParticipantInsert toMatchParticipantInsert(UserProfile userProfile, MatchCondition matchCondition) {
        return MatchParticipantInsert.builder()
                .userProfile(userProfile)
                .condition(toMatchSuccessConditionInsert(matchCondition))
                .build();
    }

    private static MatchSuccessConditionInsert toMatchSuccessConditionInsert(MatchCondition matchCondition) {
        return MatchSuccessConditionInsert.builder()
                .gameSkillLevel(matchCondition.getGameSkillLevel())
                .isAdultMatchAllowed(matchCondition.getIsAdultMatchAllowed())
                .isVoice(matchCondition.getIsVoice())
                .minMannerScore(matchCondition.getMinMannerScore())
                .playStyle(matchCondition.getPlayStyle())
                .gamePlatforms(matchCondition.getGamePlatforms())
                .preferredGenres(matchCondition.getPreferredGenres())
                .build();
    }
}
