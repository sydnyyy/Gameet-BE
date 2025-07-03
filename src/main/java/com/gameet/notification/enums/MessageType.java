package com.gameet.notification.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MessageType {

    MATCH_RESULT("매칭 결과"),
    CHAT("채팅"),
    MATCH_APPOINTMENT("매칭 약속"),

    STOMP_SUBSCRIBE("STOMP 구독"),

    STOMP_SUBSCRIBE_USER("개인 구독 완료"),
    STOMP_SUBSCRIBE_TOPIC("단체 구독 완료"),

    ERROR_EMPTY_DESTINATION("구독 경로 비어있음"),
    ERROR_INVALID_DESTINATION("지원하지 않는 구독 경로")
    ;

    private final String name;
}
