package com.gameet.global.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GameSkillLevel {

    BEGINNER("초보"),
    INTERMEDIATE("중급"),
    ADVANCED("상급"),
    EXPERT("고수"),
    PROFESSIONAL("프로");

    private final String name;
}
