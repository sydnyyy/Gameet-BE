package com.gameet.user.dto;

import com.gameet.global.enums.GamePlatform;
import com.gameet.global.enums.GameSkillLevel;
import com.gameet.global.enums.PlayStyle;
import com.gameet.global.enums.PreferredGenre;
import lombok.Builder;

import java.util.List;

@Builder
public record UserProfileUpdateRequest (

        String nickname,
        Integer age,
        Boolean showAge,
        String gender,
        List<PreferredGenre> preferredGenres,
        List<GamePlatform> platforms,
        PlayStyle playStyle,
        GameSkillLevel gameSkillLevel,
        Boolean isAdultMatchAllowed,
        Boolean isVoice
) {
}
