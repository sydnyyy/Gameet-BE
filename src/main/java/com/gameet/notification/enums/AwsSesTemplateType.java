package com.gameet.notification.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AwsSesTemplateType {

    MATCH_RESULT("MatchResultNotification"),
    APPOINTMENT("AppointmentNotification"),
    EMAIL_VERIFICATION("EmailVerificationNotification")
    ;

    private final String templateName;
}
