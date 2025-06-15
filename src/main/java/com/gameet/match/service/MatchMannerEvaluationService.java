package com.gameet.match.service;

import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.match.dto.insert.MatchMannerEvaluationLogInsert;
import com.gameet.match.dto.request.MatchMannerEvaluationRequest;
import com.gameet.match.entity.MatchMannerEvaluationLog;
import com.gameet.match.entity.MatchRoom;
import com.gameet.match.repository.MatchMannerEvaluationLogRepository;
import com.gameet.match.repository.MatchRoomRepository;
import com.gameet.user.entity.UserProfile;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchMannerEvaluationService {

    private final MatchMannerEvaluationLogRepository matchMannerEvaluationLogRepository;
    private final MatchRoomRepository matchRoomRepository;

    @Transactional
    public void mannerEvaluation(Long userId, MatchMannerEvaluationRequest matchMannerEvaluationRequest) {
        MatchRoom matchRoom = matchRoomRepository.findById(matchMannerEvaluationRequest.matchRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MATCH_ROOM));

        matchRoom.getMatchParticipants().forEach(matchParticipant -> {
            UserProfile userProfile = matchParticipant.getUserProfile();
            if (!userProfile.getUserProfileId().equals(userId)) {
                Integer evaluationScore = matchMannerEvaluationRequest.mannerEvaluation().getEvaluationScore();
                userProfile.updateMannerScore(userProfile.getMannerScore() + evaluationScore);
            } else {
                boolean alreadyEvaluated = matchMannerEvaluationLogRepository.existsById(matchParticipant.getMatchParticipantId());

                if (alreadyEvaluated) {
                    throw new CustomException(ErrorCode.ALREADY_MATCH_MANNER_EVALUATION);
                }

                MatchMannerEvaluationLog matchMannerEvaluationLog = MatchMannerEvaluationLog.of(MatchMannerEvaluationLogInsert.builder()
                        .matchParticipant(matchParticipant)
                        .mannerEvaluation(matchMannerEvaluationRequest.mannerEvaluation())
                        .build());
                matchMannerEvaluationLogRepository.save(matchMannerEvaluationLog);
            }
        });
    }
}
