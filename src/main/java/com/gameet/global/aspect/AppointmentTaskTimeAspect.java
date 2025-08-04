package com.gameet.global.aspect;

import com.gameet.global.annotation.TrackAppointmentTaskTime;
import com.gameet.global.metrics.AppointmentTaskTimeStore;
import com.gameet.match.entity.MatchAppointment;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class AppointmentTaskTimeAspect {

    private final AppointmentTaskTimeStore appointmentTaskTimeStore;

    @Around("@annotation(com.gameet.global.annotation.TrackAppointmentTaskTime)")
    public Object trackExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        Object result = null;

        TrackAppointmentTaskTime annotation = getAnnotation(joinPoint);
        TrackAppointmentTaskTime.TaskType taskType = Objects.requireNonNull(annotation).taskType();

        try {
            stopWatch.start();
            result = joinPoint.proceed();
        } finally {
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();

            if (TrackAppointmentTaskTime.TaskType.DB_SCAN == taskType) {
                List<MatchAppointment> appointments = (List<MatchAppointment>) result;
                if (appointments != null && !appointments.isEmpty()) {
                    LocalDateTime appointmentAt = appointments.getFirst().getAppointmentAt();
                    appointmentTaskTimeStore.addDbScanMetrics(
                            appointmentAt,
                            AppointmentTaskTimeStore.ExecutionData.builder()
                                    .recordCount(appointments.size())
                                    .executionTimeMillis(totalTimeMillis)
                                    .build());
                }
            } else if (TrackAppointmentTaskTime.TaskType.ASYNC_EMAIL_SEND == taskType) {
                List<MatchAppointment> appointments = getAsyncEmailSendTypeParameters(joinPoint);
                if (appointments != null && !appointments.isEmpty()) {
                    LocalDateTime appointmentAt = appointments.getFirst().getAppointmentAt();
                    appointmentTaskTimeStore.sendExecutionLog(
                            appointmentAt,
                            AppointmentTaskTimeStore.ExecutionData.builder()
                                    .recordCount(appointments.size() * 2)
                                    .executionTimeMillis(totalTimeMillis)
                                    .build());
                }
            }
        }
        return result;
    }

    private TrackAppointmentTaskTime getAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(TrackAppointmentTaskTime.class);
    }

    private List<MatchAppointment> getAsyncEmailSendTypeParameters(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof List<?> list) {
            if (!list.isEmpty() && list.getFirst() instanceof MatchAppointment) {
                return (List<MatchAppointment>) list;
            }
        }
        return null;
    }
}
