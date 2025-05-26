package com.gameet.match.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MatchStatus {

    NONE("상태 없음"),
    SEARCHING("매칭 중"),
    CANCEL("매칭 취소"),
    MATCHED("매칭 완료"),
    FAILED("매칭 실패"),
    COMPLETED("매칭 종료");

    private final String description;
}
