package com.gameet.global.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PlayStyle {

    CASUAL("즐겜"),
    COMPETITIVE("빡겜"),
    FRIENDSHIP("친목");

    private final String name;
}
