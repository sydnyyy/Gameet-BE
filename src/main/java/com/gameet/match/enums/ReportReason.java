package com.gameet.match.enums;

import com.gameet.common.enums.base.BaseCodeEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ReportReason implements BaseCodeEnum {

    ABUSIVE_LANGUAGE("욕설 및 모욕성 언어 사용"),
    CHEATING("핵, 불법 프로그램, 치트 사용"),
    INAPPROPRIATE_CONTENT("부적절한 닉네임/채팅 내용"),
    SPAM("도배, 광고성 메시지"),
    AFK("잠수, 무단 이탈"),
    INTENTIONAL_FEEDING("고의적 트롤링, 고의적인 패배 유도");

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
