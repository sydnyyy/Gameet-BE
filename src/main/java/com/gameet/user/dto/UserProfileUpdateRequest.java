package com.gameet.user.dto;

import com.gameet.global.enums.GamePlatform;
import com.gameet.global.enums.GameSkillLevel;
import com.gameet.global.enums.PlayStyle;
import com.gameet.global.enums.PreferredGenre;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.List;

@Builder
public record UserProfileUpdateRequest (

        String nickname,
        Integer age,
        Boolean showAge,

        @Pattern(regexp = "^[FMN]$", message = "성별은 F, M, N 중 하나여야 합니다.")
        String gender,

        List<PreferredGenre> preferredGenres,
        List<GamePlatform> platforms,
        PlayStyle playStyle,
        GameSkillLevel gameSkillLevel,
        Boolean isAdultMatchAllowed,
        Boolean isVoice
) {
}
