package com.gameet.user.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {

    GUEST("GUEST"),
    USER("USER"),
    ADMIN("ADMIN");

    private final String description;
}
