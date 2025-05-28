package com.gameet.common.enums;

import com.gameet.common.enums.base.BaseCodeEnum;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GameSkillLevel implements BaseCodeEnum {

    BEGINNER("초보", 1),
    INTERMEDIATE("중급", 2),
    ADVANCED("상급", 3),
    EXPERT("고수", 4),
    PROFESSIONAL("프로", 5);

    private final String name;
	private final int sortOrder;

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getSortOrder() {
        return sortOrder;
    }

}
