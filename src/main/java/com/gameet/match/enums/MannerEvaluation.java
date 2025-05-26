package com.gameet.match.enums;

import com.gameet.common.enums.base.BaseCodeEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MannerEvaluation implements BaseCodeEnum {

    VERY_GOOD("매우 좋음 (예: 매너가 뛰어남)"),
    GOOD("좋음 (예: 괜찮은 매너)"),
    AVERAGE("보통 (예: 큰 문제 없음)"),
    POOR("나쁨 (예: 일부 비매너 행동)"),
    VERY_POOR("매우 나쁨 (예: 지속적인 비매너)");

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
