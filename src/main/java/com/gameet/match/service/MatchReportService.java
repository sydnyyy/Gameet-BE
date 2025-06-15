package com.gameet.match.service;

import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.match.dto.insert.MatchReportLogInsert;
import com.gameet.match.dto.request.MatchReportRequest;
import com.gameet.match.entity.MatchReportLog;
import com.gameet.match.entity.MatchRoom;
import com.gameet.match.enums.ReportReason;
import com.gameet.match.repository.MatchReportLogRepository;
import com.gameet.match.repository.MatchRoomRepository;
import com.gameet.user.entity.UserProfile;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchReportService {

    private final MatchReportLogRepository matchReportLogRepository;
    private final MatchRoomRepository matchRoomRepository;

    @Transactional
    public void report(Long userId, MatchReportRequest matchReportRequest) {
        MatchRoom matchRoom = matchRoomRepository.findById(matchReportRequest.matchRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MATCH_ROOM));

        matchRoom.getMatchParticipants().forEach(matchParticipant -> {
            UserProfile userProfile = matchParticipant.getUserProfile();
            List<ReportReason> reportReasons = matchReportRequest.reportReasons();
            if (!userProfile.getUserProfileId().equals(userId)) {
                Integer totalPenaltyScore = reportReasons.stream()
                        .mapToInt(ReportReason::getPenaltyScore)
                        .sum();
                userProfile.updateMannerScore(userProfile.getMannerScore() + totalPenaltyScore);
            } else {
                boolean alreadyReported = matchReportLogRepository.existsByMatchParticipant_MatchParticipantId(matchParticipant.getMatchParticipantId());

                if (alreadyReported) {
                    throw new CustomException(ErrorCode.ALREADY_MATCH_REPORT);
                }

                List<MatchReportLog> matchReportLogs = reportReasons.stream()
                        .map(reportReason -> MatchReportLog.of(MatchReportLogInsert.builder()
                                .matchParticipant(matchParticipant)
                                .reportReason(reportReason)
                                .build())).toList();
                matchReportLogRepository.saveAll(matchReportLogs);
            }
        });
    }
}
