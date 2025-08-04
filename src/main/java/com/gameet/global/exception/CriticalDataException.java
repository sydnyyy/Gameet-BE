package com.gameet.global.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class CriticalDataException extends RuntimeException {

    private final List<Long> userIds;

    public CriticalDataException(String message, List<Long> userIds) {
        super(message);
        this.userIds = userIds;
    }
}
