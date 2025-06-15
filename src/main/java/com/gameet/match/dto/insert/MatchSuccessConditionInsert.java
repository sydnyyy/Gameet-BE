package com.gameet.match.dto.insert;

import com.gameet.common.enums.GamePlatform;
import com.gameet.common.enums.GameSkillLevel;
import com.gameet.common.enums.PlayStyle;
import com.gameet.common.enums.PreferredGenre;
import lombok.Builder;

import java.util.List;

@Builder
public record MatchSuccessConditionInsert(
        GameSkillLevel gameSkillLevel,
        Boolean isAdultMatchAllowed,
        Boolean isVoice,
        Integer minMannerScore,
        PlayStyle playStyle,
        List<GamePlatform> gamePlatforms,
        List<PreferredGenre> preferredGenres
) {
}
