package com.gameet.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackAppointmentTaskTime {

    TaskType taskType();

    enum TaskType {
        DB_SCAN,
        ASYNC_EMAIL_SEND
    }
}
