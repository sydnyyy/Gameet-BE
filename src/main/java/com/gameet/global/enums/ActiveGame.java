package com.gameet.global.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ActiveGame {

    LEAGUE_OF_LEGENDS("리그 오브 레전드"),
    VALORANT("발로란트"),
    FORTNITE("포트나이트"),
    MINECRAFT("마인크래프트"),
    FIFA("피파"),
    GENSHIN_IMPACT("원신"),
    APEX_LEGENDS("에이펙스 레전드"),
    CALL_OF_DUTY("콜 오브 듀티"),
    AMONG_US("어몽 어스"),
    ANIMAL_CROSSING("동물의 숲");

    private final String name;
}
