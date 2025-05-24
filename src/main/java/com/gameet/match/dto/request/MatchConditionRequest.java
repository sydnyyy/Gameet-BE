package com.gameet.match.dto.request;

import com.gameet.common.enums.GamePlatform;
import com.gameet.common.enums.GameSkillLevel;
import com.gameet.common.enums.PlayStyle;
import com.gameet.common.enums.PreferredGenre;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record MatchConditionRequest(

        @NotEmpty(message = "선호 장르는 필수입니다.")
        List<PreferredGenre> preferredGenres,

        @NotEmpty(message = "플랫폼은 필수입니다.")
        List<GamePlatform> gamePlatforms,

        @NotNull(message = "플레이 스타일은 필수입니다.")
        PlayStyle playStyle,

        @NotNull(message = "게임 실력은 필수입니다.")
        GameSkillLevel gameSkillLevel,

        @NotNull(message = "매칭 상대 미성년 여부는 필수입니다.")
        Boolean isAdultMatchAllowed,

        @NotNull(message = "마이크 여부는 필수입니다.")
        Boolean isVoice,

        @NotNull(message = "매너 점수 최소치는 필수입니다.")
        Integer minMannerScore
) {
}
