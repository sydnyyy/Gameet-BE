package com.gameet.user.dto.response;

import com.gameet.common.enums.GameSkillLevel;
import com.gameet.common.enums.PlayStyle;
import com.gameet.common.enums.PreferredGenre;
import com.gameet.user.entity.UserPreferredGenre;
import com.gameet.user.entity.UserProfile;
import lombok.Builder;

import java.util.List;

@Builder
public record UserPublicProfileResponse (

        String nickname,
        List<PreferredGenre> preferredGenres,
        PlayStyle playStyle,
        GameSkillLevel gameSkillLevel,
        Boolean isVoice,
        Boolean isAdultMatchAllowed
) {

    public static UserPublicProfileResponse of(UserProfile userProfile) {
        List<PreferredGenre> preferredGenres = userProfile.getPreferredGenres()
                .stream()
                .map(UserPreferredGenre::getPreferredGenre)
                .toList();

        return UserPublicProfileResponse.builder()
                .nickname(userProfile.getNickname())
                .preferredGenres(preferredGenres)
                .playStyle(userProfile.getPlayStyle())
                .gameSkillLevel(userProfile.getGameSkillLevel())
                .isVoice(userProfile.getIsVoice())
                .isAdultMatchAllowed(userProfile.getIsAdultMatchAllowed())
                .build();
    }
}
