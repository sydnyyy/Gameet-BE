package com.gameet.user.dto.request;

import com.gameet.common.enums.GamePlatform;
import com.gameet.common.enums.GameSkillLevel;
import com.gameet.common.enums.PlayStyle;
import com.gameet.common.enums.PreferredGenre;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;

@Builder
public record UserProfileRequest (

        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @NotNull(message = "나이는 필수입니다.")
        @Positive(message = "나이는 1살 이상이어야 합니다.")
        Integer age,

        @NotNull(message = "나이 공개 여부는 필수입니다.")
        Boolean showAge,

        @NotNull(message = "성별은 필수입니다.")
        @Pattern(regexp = "^[FMN]$", message = "성별은 F, M, N 중 하나여야 합니다.")
        String gender,

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
        Boolean isVoice
) {
}
