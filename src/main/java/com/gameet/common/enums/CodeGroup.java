package com.gameet.common.enums;

import com.gameet.common.enums.base.BaseCodeEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CodeGroup implements BaseCodeEnum {

    MATCH_CONDITION("매칭 조건"),
    PREFERRED_GENRE("선호 장르"),
    GAME_PLATFORM("게임 플랫폼"),
    GAME_SKILL_LEVEL("게임 실력"),
    PLAY_STYLE("플레이 스타일"),
    REPORT_REASON("신고 사유"),
    MANNER_EVALUATION("매너 평가");

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
