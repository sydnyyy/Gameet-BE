package com.gameet.common.enums;

import com.gameet.common.enums.base.BaseCodeEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GamePlatform implements BaseCodeEnum {

    PC("PC"),
    CONSOLE("콘솔"),
    MOBILE("모바일"),
    VR("가상현실");

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
