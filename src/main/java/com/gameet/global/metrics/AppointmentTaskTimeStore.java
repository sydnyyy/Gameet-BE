package com.gameet.global.metrics;

import com.gameet.common.enums.AlertLevel;
import com.gameet.common.service.DiscordNotifier;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class AppointmentTaskTimeStore {

    private final Map<LocalDateTime, ExecutionData> dbScanMetrics = new ConcurrentHashMap<>();
    private final DiscordNotifier discordNotifier;

    public void addDbScanMetrics(LocalDateTime dateTime, ExecutionData executionData) {
        dbScanMetrics.put(dateTime, executionData);
    }

    public void sendExecutionLog(LocalDateTime dateTime, ExecutionData executionData) {
        ExecutionData dbScanExecutionData = dbScanMetrics.get(dateTime);
        dbScanMetrics.remove(dateTime);
        String formattedDateTime = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        String title = "📧 약속 알림 전송 성능 결과";
        String message = String.format(
                        """
                        약속 그룹 스캔
                        - 그룹 수: %,d 건
                        - 소요 시간: %,d ms
                        
                        %s 알림 전송
                        - 사용자 수: %,d 명
                        - 소요 시간: %,d ms
                        
                        ✅ %s 시각에 해당하는 %,d개 그룹의 %,d명 사용자에게 알림 발송 로직 총 %,d ms 소요
                        """,
                dbScanExecutionData.recordCount(),
                dbScanExecutionData.executionTimeMillis(),

                formattedDateTime,
                executionData.recordCount(),
                executionData.executionTimeMillis(),

                formattedDateTime,
                dbScanExecutionData.recordCount(),
                executionData.recordCount(),
                dbScanExecutionData.executionTimeMillis() + executionData.executionTimeMillis()
        );

        discordNotifier.send(title, message, AlertLevel.INFO);
    }

    @Builder
    public record ExecutionData (
            LocalDateTime appointmentAt,
            int recordCount,
            long executionTimeMillis
    ) { }
}
