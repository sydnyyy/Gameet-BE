package com.gameet.match.enums;

import com.gameet.common.enums.base.BaseCodeEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MannerEvaluation implements BaseCodeEnum {

    NONE("평가하지 않음", 0, 1),
    VERY_GOOD("매우 좋음 (예: 매너가 뛰어남)", 20, 2),
    GOOD("좋음 (예: 괜찮은 매너)", 15, 3),
    AVERAGE("보통 (예: 큰 문제 없음)", 10, 4),
    POOR("나쁨 (예: 일부 비매너 행동)", -15, 5),
    VERY_POOR("매우 나쁨 (예: 지속적인 비매너)", -20, 6);

    private final String name;
    @Getter
    private final Integer evaluationScore;
    private final Integer sortOrder;

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
