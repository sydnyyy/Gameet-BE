package com.gameet.auth.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {

    USER("USER"),
    ADMIN("ADMIN");

    private final String description;
}
