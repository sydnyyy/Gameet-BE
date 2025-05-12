package com.gameet.global.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GamePlatform {

    PC("PC"),
    CONSOLE("콘솔"),
    MOBILE("모바일"),
    VR("가상현실");

    private final String name;
}
