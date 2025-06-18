package com.gameet.match.repository;

import com.gameet.match.entity.MatchReportLog;
import com.gameet.match.entity.MatchReportLogId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchReportLogRepository extends JpaRepository<MatchReportLog, MatchReportLogId> {

    boolean existsByMatchParticipant_MatchParticipantId(Long matchParticipantMatchParticipantId);
}
