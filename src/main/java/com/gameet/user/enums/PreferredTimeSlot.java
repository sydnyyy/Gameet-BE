package com.gameet.user.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PreferredTimeSlot {

    DAWN("00:00 ~ 06:00"),
    MORNING("06:00 ~ 12:00"),
    LATE_MORNING("10:00 ~ 14:00"),
    AFTERNOON("12:00 ~ 18:00"),
    LATE_AFTERNOON("14:00 ~ 20:00"),
    EVENING("16:00 ~ 22:00"),
    NIGHT("18:00 ~ 24:00");

    private final String displayTimeRange;
}
