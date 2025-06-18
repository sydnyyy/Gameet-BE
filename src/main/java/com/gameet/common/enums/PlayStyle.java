package com.gameet.common.enums;

import com.gameet.common.enums.base.BaseCodeEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PlayStyle implements BaseCodeEnum {

    CASUAL("즐겜"),
    COMPETITIVE("빡겜"),
    FRIENDSHIP("친목");

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
