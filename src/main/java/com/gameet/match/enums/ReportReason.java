package com.gameet.match.enums;

import com.gameet.common.enums.base.BaseCodeEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReportReason implements BaseCodeEnum {

    ABUSIVE_LANGUAGE("욕설 및 모욕성 언어 사용", -10),
    CHEATING("핵, 불법 프로그램, 치트 사용", -15),
    INAPPROPRIATE_CONTENT("부적절한 닉네임/채팅 내용", -5),
    SPAM("도배, 광고성 메시지", -5),
    AFK("잠수, 무단 이탈", -15),
    INTENTIONAL_FEEDING("고의적 트롤링, 고의적인 패배 유도", -20);

    private final String name;
    @Getter
    private final Integer penaltyScore;

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getName() {
        return name;
    }
}
