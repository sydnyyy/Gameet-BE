package com.gameet.notification.repository;

import com.gameet.notification.entity.MatchEmailSendLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchEmailSendLogRepository extends JpaRepository<MatchEmailSendLog, Long> {
}
