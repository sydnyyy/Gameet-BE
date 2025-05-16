package com.gameet.common.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PreferredGenre {

    RPG("롤플레잉"),
    FPS("1인칭 슈팅"),
    MOBA("멀티플레이 온라인 배틀 아레나"),
    SPORTS("스포츠"),
    RACING("레이싱"),
    PUZZLE("퍼즐"),
    SIMULATION("시뮬레이션"),
    HORROR("공포"),
    SANDBOX("샌드박스"),
    ADVENTURE("어드벤처"),
    STEAM("스팀");

    private final String name;
}
