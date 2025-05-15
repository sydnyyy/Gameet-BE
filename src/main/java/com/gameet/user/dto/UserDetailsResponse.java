package com.gameet.user.dto;

import com.gameet.auth.enums.Role;
import com.gameet.global.enums.GamePlatform;
import com.gameet.global.enums.GameSkillLevel;
import com.gameet.global.enums.PlayStyle;
import com.gameet.global.enums.PreferredGenre;
import com.gameet.user.entity.User;
import com.gameet.user.entity.UserGamePlatform;
import com.gameet.user.entity.UserPreferredGenre;
import lombok.Builder;

import java.util.List;

@Builder
public record UserDetailsResponse (

        long userId,
        Role role,
        String email,
        String nickname,
        int age,
        Boolean showAge,
        Character gender,
        int mannerScore,
        List<PreferredGenre> preferredGenres,
        List<GamePlatform> gamePlatforms,
        PlayStyle playStyle,
        GameSkillLevel gameSkillLevel,
        Boolean isAdultMatchAllowed,
        Boolean isVoice
) {

    public static UserDetailsResponse of(User user) {
       List<PreferredGenre> preferredGenres = user.getUserProfile().getPreferredGenres()
               .stream()
               .map(UserPreferredGenre::getPreferredGenre)
               .toList();

       List<GamePlatform> gamePlatforms = user.getUserProfile().getGamePlatforms()
               .stream()
               .map(UserGamePlatform::getGamePlatform)
               .toList();

        return UserDetailsResponse.builder()
                .userId(user.getUserId())
                .role(user.getRole())
                .email(user.getEmail())
                .nickname(user.getUserProfile().getNickname())
                .age(user.getUserProfile().getAge())
                .showAge(user.getUserProfile().getShowAge())
                .gender(user.getUserProfile().getGender())
                .mannerScore(user.getUserProfile().getMannerScore())
                .preferredGenres(preferredGenres)
                .gamePlatforms(gamePlatforms)
                .playStyle(user.getUserProfile().getPlayStyle())
                .gameSkillLevel(user.getUserProfile().getGameSkillLevel())
                .isAdultMatchAllowed(user.getUserProfile().getIsAdultMatchAllowed())
                .isVoice(user.getUserProfile().getIsVoice())
                .build();
    }
}
