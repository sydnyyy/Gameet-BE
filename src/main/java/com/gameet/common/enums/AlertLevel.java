package com.gameet.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlertLevel {

    INFO(0x0000FF),
    CRITICAL(0xFF0000);

    private final int color;
}
