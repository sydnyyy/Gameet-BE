package com.gameet.notification.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MessageType {

    MATCH_RESULT("매칭 결과"),
    MATCH_APPOINTMENT("매칭 약속")
    ;

    private final String name;
}
