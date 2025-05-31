package com.gameet.global.dto;

import lombok.Builder;

@Builder
public record StompSubscribePayload (
        String destination,
        String message
) {

    public static StompSubscribePayload of(String destination, String message) {
        return StompSubscribePayload.builder()
                .destination(destination)
                .message(message)
                .build();
    }
}
