package com.gameet.common.enums;

import com.gameet.common.enums.base.BaseCodeEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GameSkillLevel implements BaseCodeEnum {

    BEGINNER("초보"),
    INTERMEDIATE("중급"),
    ADVANCED("상급"),
    EXPERT("고수"),
    PROFESSIONAL("프로");

    private final String name;

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getName() {
        return name;
    }
}
