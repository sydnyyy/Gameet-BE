package com.gameet.match.domain;

import com.gameet.common.enums.GamePlatform;
import com.gameet.common.enums.GameSkillLevel;
import com.gameet.common.enums.PlayStyle;
import com.gameet.common.enums.PreferredGenre;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class MatchCondition {
    private List<PreferredGenre> preferredGenres;
    private List<GamePlatform> gamePlatforms;
    private PlayStyle playStyle;
    private GameSkillLevel gameSkillLevel;
    private Boolean isAdultMatchAllowed;
    private Integer age;
    private Boolean isVoice;
    private Integer minMannerScore;
    private Integer mannerScore;
}
