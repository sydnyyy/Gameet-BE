package com.gameet.user.dto.request;

import com.gameet.common.enums.GamePlatform;
import com.gameet.common.enums.GameSkillLevel;
import com.gameet.common.enums.PlayStyle;
import com.gameet.common.enums.PreferredGenre;
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
        List<GamePlatform> gamePlatforms,
        PlayStyle playStyle,
        GameSkillLevel gameSkillLevel,
        Boolean isAdultMatchAllowed,
        Boolean isVoice
) {
}
