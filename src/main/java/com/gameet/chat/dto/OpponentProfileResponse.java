package com.gameet.chat.dto;

import com.gameet.common.enums.GamePlatform;
import com.gameet.common.enums.GameSkillLevel;
import com.gameet.common.enums.PlayStyle;
import com.gameet.common.enums.PreferredGenre;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OpponentProfileResponse {
    private Long userProfileId;
    private String nickname;
    private Integer age;
    private Boolean showAge;
    private Character gender;
    private List<PreferredGenre> preferredGenres;
    private List<GamePlatform> gamePlatforms;
    private PlayStyle playStyle;
    private GameSkillLevel gameSkillLevel;
    private Boolean isAdultMatchAllowed;
    private Boolean isVoice;
}
