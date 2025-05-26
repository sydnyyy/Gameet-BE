package com.gameet.match.service;

import com.gameet.common.enums.GamePlatform;
import com.gameet.common.enums.GameSkillLevel;
import com.gameet.common.enums.PlayStyle;
import com.gameet.common.enums.PreferredGenre;
import com.gameet.match.domain.MatchCondition;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class MatchConditionMatcher {
    public static boolean isMatchCompatible(MatchCondition a, MatchCondition b) {
        return isPreferredGenreMatch(a.getPreferredGenres(), b.getPreferredGenres()) &&
                isPlatformMatch(a.getGamePlatforms(), b.getGamePlatforms()) &&
                isPlayStyleMatch(a.getPlayStyle(), b.getPlayStyle()) &&
                isGameSkillLevelMatch(a.getGameSkillLevel(), b.getGameSkillLevel()) &&
                isAdultMatchAllowedMatch(a.getIsAdultMatchAllowed(), a.getAge(), b.getIsAdultMatchAllowed(), b.getAge()) &&
                isVoiceMatch(a.getIsVoice(), b.getIsVoice()) &&
                isMannerScoreMatch(a.getMinMannerScore(), a.getMannerScore(), b.getMinMannerScore(), b.getMannerScore());
    }

    private static boolean isPreferredGenreMatch(List<PreferredGenre> genresA , List<PreferredGenre> genresB) {
        if (CollectionUtils.isEmpty(genresA) || CollectionUtils.isEmpty(genresB)) {
            return true;
        }

        return genresA.stream().anyMatch(genresB::contains);
    }

    private static boolean isPlatformMatch(List<GamePlatform> platformsA , List<GamePlatform> platformsB) {
        if (CollectionUtils.isEmpty(platformsA) || CollectionUtils.isEmpty(platformsB)) {
            return true;
        }

        return platformsA.stream().anyMatch(platformsB::contains);
    }

    private static boolean isPlayStyleMatch(PlayStyle playStyleA, PlayStyle playStyleB) {
        if (playStyleA == null || playStyleB == null) {
            return true;
        }

        return playStyleA == playStyleB;
    }

    private static boolean isGameSkillLevelMatch(GameSkillLevel gameSkillLevelA, GameSkillLevel gameSkillLevelB) {
        if (gameSkillLevelA == null || gameSkillLevelB == null) {
            return true;
        }

        return gameSkillLevelA == gameSkillLevelB;
    }

    private static boolean isAdultMatchAllowedMatch(
            Boolean isAdultMatchAllowedA, Integer ageA,
            Boolean isAdultMatchAllowedB, Integer ageB) {
        if (isAdultMatchAllowedA == null || isAdultMatchAllowedB == null) {
            return true;
        }

        if (isAdultMatchAllowedA && ageB < 20) {
            return false;
        }

        if (isAdultMatchAllowedB && ageA < 20) {
            return false;
        }

        return true;
    }

    private static boolean isVoiceMatch(Boolean isVoiceA, Boolean isVoiceB) {
        if (isVoiceA == null || isVoiceB == null) {
            return true;
        }

        return isVoiceA == isVoiceB;
    }

    private static boolean isMannerScoreMatch(
            Integer minMannerScoreA, Integer mannerScoreA,
            Integer minMannerScoreB, Integer mannerScoreB) {
        if (minMannerScoreA == null || minMannerScoreB == null) {
            return true;
        }

        return mannerScoreB >= minMannerScoreA && mannerScoreA >= minMannerScoreB;
    }
}
