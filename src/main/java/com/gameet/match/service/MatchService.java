package com.gameet.match.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.match.annotation.MatchUserLockable;
import com.gameet.match.domain.MatchCondition;
import com.gameet.match.dto.request.MatchAppointmentRequest;
import com.gameet.match.dto.request.MatchConditionRequest;
import com.gameet.match.dto.request.MatchParticipantInsert;
import com.gameet.match.dto.request.MatchRoomInsert;
import com.gameet.match.dto.response.MatchAppointmentResponse;
import com.gameet.match.dto.response.MatchStatusWithInfoResponse;
import com.gameet.match.entity.MatchAppointment;
import com.gameet.match.entity.MatchRoom;
import com.gameet.match.enums.MatchStatus;
import com.gameet.match.repository.MatchAppointmentRepository;
import com.gameet.match.repository.MatchParticipantRepository;
import com.gameet.match.repository.MatchRepository;
import com.gameet.match.repository.MatchRoomRepository;
import com.gameet.match.util.MatchConditionMatcher;
import com.gameet.match.util.MatchMapper;
import com.gameet.notification.service.NotificationService;
import com.gameet.user.entity.UserProfile;
import com.gameet.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchService {

    private final MatchRepository matchRepository;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(4);
    private final MatchRoomRepository matchRoomRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final UserProfileRepository userProfileRepository;
    private final NotificationService notificationService;
    private final MatchAppointmentRepository matchAppointmentRepository;

    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    private final ObjectProvider<MatchService> serviceProvider;

    private MatchService self() {
        return serviceProvider.getIfAvailable();
    }

    public void tryMatch(Long userId, MatchConditionRequest matchConditionRequest) {
        log.info("[매칭 시도 시작] 사용자 ID: {}", userId);
        MatchStatus matchStatus = getMatchStatus(userId);
        if (matchStatus == MatchStatus.SEARCHING) {
            throw new CustomException(ErrorCode.ALREADY_SEARCHING);
        } else if (matchStatus == MatchStatus.MATCHED) {
            throw new CustomException(ErrorCode.ALREADY_MATCHED);
        }

        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        MatchCondition matchCondition = MatchMapper.toMatchCondition(userProfile, matchConditionRequest);

        Set<Long> allMatchUsers = matchRepository.getAllMatchUsers();

        for (Long otherMatchUserId : allMatchUsers) {
            if (matchRepository.tryLock(otherMatchUserId)) {
                try {
                    MatchCondition otherMatchCondition = matchRepository.getMatchConditionByUserId(otherMatchUserId);

                    boolean isMatchCompatible = MatchConditionMatcher.isMatchCompatible(otherMatchCondition, matchCondition);
                    if (isMatchCompatible) {
                        log.info("[매칭 성공] 사용자 {} ↔ {}", userId, otherMatchUserId);
                        removeMatch(otherMatchUserId);

                        cancelScheduledTask(otherMatchUserId);

                        UserProfile otherUserProfile = userProfileRepository.getReferenceById(otherMatchUserId);

                        List<MatchParticipantInsert> matchParticipantInserts = List.of(
                                MatchMapper.toMatchParticipantInsert(userProfile, matchCondition),
                                MatchMapper.toMatchParticipantInsert(otherUserProfile, otherMatchCondition)
                        );

                        Long matchRoomId = createMatchRoom(matchParticipantInserts);
                        notificationService.sendMatchResult(List.of(userId, otherMatchUserId), MatchStatus.MATCHED, matchRoomId);

                        return;
                    }
                } finally {
                    matchRepository.releaseLock(otherMatchUserId);
                }

            }
        }

        matchRepository.addMatchUser(userId);
        matchRepository.saveMatchCondition(userId, convertMatchConditionToStringMap(matchCondition));

        ScheduledFuture<?> future = scheduledExecutorService.schedule(() -> retryMatchWithRelaxedCondition(userId, matchCondition), 30, TimeUnit.SECONDS);
        scheduledTasks.put(userId, future);
        log.info("[매칭 시도 종료] 사용자 ID: {}", userId);
    }

    private void retryMatchWithRelaxedCondition(Long userId, MatchCondition matchCondition) {
        List<Runnable> RelaxedConditionSteps = List.of(
                () -> matchCondition.setGameSkillLevel(null),
                () -> matchCondition.setPlayStyle(null),
                () -> matchCondition.setIsVoice(null)
        );

        self().retryMatch(userId, matchCondition, RelaxedConditionSteps, 0);
    }

    @MatchUserLockable
    protected void retryMatch(Long userId, MatchCondition matchCondition, List<Runnable> steps, int stepIndex) {
        if (stepIndex >= steps.size()) {
            ScheduledFuture<?> future = scheduledExecutorService.schedule(() -> self().failMatch(userId), 15, TimeUnit.SECONDS);
            scheduledTasks.put(userId, future);
            return;
        }

        log.info("[재시도 {}단계 시작] 사용자 ID: {}", stepIndex + 1, userId);

        steps.get(stepIndex).run();

        Set<Long> allMatchUsers = matchRepository.getAllMatchUsers();

        for (Long otherMatchUserId : allMatchUsers) {
            if (otherMatchUserId.equals(userId)) {
                continue;
            }
            if (matchRepository.tryLock(otherMatchUserId)) {
                try {
                    MatchCondition otherMatchCondition = matchRepository.getMatchConditionByUserId(otherMatchUserId);
                    boolean isMatchCompatible = MatchConditionMatcher.isMatchCompatible(otherMatchCondition, matchCondition);
                    if (isMatchCompatible && matchRepository.isMatchUserExists(userId)) {
                        log.info("[매칭 성공] 사용자 {} ↔ {}", userId, otherMatchUserId);

                        removeMatch(userId);
                        removeMatch(otherMatchUserId);

                        cancelScheduledTask(otherMatchUserId);

                        UserProfile userProfile = userProfileRepository.getReferenceById(userId);
                        UserProfile otherUserProfile = userProfileRepository.getReferenceById(otherMatchUserId);

                        List<MatchParticipantInsert> matchParticipantInserts = List.of(
                                MatchMapper.toMatchParticipantInsert(userProfile, matchCondition),
                                MatchMapper.toMatchParticipantInsert(otherUserProfile, otherMatchCondition)
                        );

                        Long matchRoomId = createMatchRoom(matchParticipantInserts);
                        notificationService.sendMatchResult(List.of(userId, otherMatchUserId), MatchStatus.MATCHED, matchRoomId);

                        return;
                    }
                } finally {
                    matchRepository.releaseLock(otherMatchUserId);
                }
            }
        }

        matchRepository.saveMatchCondition(userId, convertMatchConditionToStringMap(matchCondition));

        ScheduledFuture<?> future = scheduledExecutorService.schedule(() ->
                self().retryMatch(userId, matchCondition, steps, stepIndex + 1), 5, TimeUnit.SECONDS
        );
        scheduledTasks.put(userId, future);
        log.info("[재시도 {}단계 종료] 사용자 ID: {}", stepIndex + 1, userId);
    }

    @MatchUserLockable
    protected void failMatch(Long userId) {
        log.info("[매칭 실패] 사용자 {}", userId);
        removeMatch(userId);
        notificationService.sendMatchResult(userId, MatchStatus.FAILED, null);
    }

    @MatchUserLockable
    public void cancelMatch(Long userId) {
        removeMatch(userId);
        cancelScheduledTask(userId);
    }

    public MatchStatusWithInfoResponse getMatchStatusWithInfo(Long userId) {
        switch (getMatchStatus(userId)) {
            case SEARCHING -> {
                Long elapsedTime = matchRepository.getElapsedTime(userId);
                return new MatchStatusWithInfoResponse(MatchStatus.SEARCHING, elapsedTime, null);
            }
            case MATCHED -> {
                Long matchRoomId = matchParticipantRepository.findMatchRoomIdByUserProfileId(userId);
                return new MatchStatusWithInfoResponse(MatchStatus.MATCHED, null, matchRoomId);
            }
            default -> {
                return new MatchStatusWithInfoResponse(MatchStatus.NONE, null, null);
            }
        }
    }

    private MatchStatus getMatchStatus(Long userId) {
        if (matchRepository.isMatchUserExists(userId)) {
            return MatchStatus.SEARCHING;
        }

        boolean isMatched = matchParticipantRepository.existsByMatchParticipantIdAndMatchRoom_matchStatus(userId, MatchStatus.MATCHED);
        if (isMatched) {
            return MatchStatus.MATCHED;
        }

        return MatchStatus.NONE;
    }

    private void removeMatch(Long userId) {
        matchRepository.removeMatchUser(userId);
        matchRepository.removeMatchCondition(userId);
    }

    private void cancelScheduledTask(Long userId) {
        ScheduledFuture<?> future = scheduledTasks.remove(userId);
        if (future != null) {
            future.cancel(false);
        }
    }

    private Long createMatchRoom(List<MatchParticipantInsert> matchParticipantInsertList) {
        MatchRoomInsert matchRoomInsert = MatchRoomInsert.builder()
                .matchStatus(MatchStatus.MATCHED)
                .participants(matchParticipantInsertList)
                .build();

        MatchRoom matchRoom = MatchRoom.of(matchRoomInsert);
        MatchRoom saved = matchRoomRepository.save(matchRoom);

        return saved.getMatchRoomId();
    }

    private Map<String, String> convertMatchConditionToStringMap(MatchCondition matchCondition) {
        Map<String, Object> map = objectMapper.convertValue(matchCondition, new TypeReference<>() {});
        Map<String, String> stringMap = new HashMap<>();
        map.forEach((key, value) -> {
            if (value instanceof List<?> list) {
                try {
                    stringMap.put(key, objectMapper.writeValueAsString(list));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                stringMap.put(key, value != null ? value.toString() : "");
            }
        });

        return stringMap;
    }

    @Transactional
    public MatchAppointmentResponse createMatchAppointment(Long userId, MatchAppointmentRequest matchAppointmentRequest) {
        MatchRoom matchRoom = matchRoomRepository.findById(matchAppointmentRequest.matchRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MATCH_ROOM));

        boolean isParticipant = matchRoom.getMatchParticipants().stream()
                .anyMatch(mp -> mp.getUserProfile().getUser().getUserId().equals(userId));

        if (!isParticipant) {
            throw new CustomException(ErrorCode.NO_AUTH_MATCH_ROOM);
        }

        if (!matchRoom.getMatchStatus().equals(MatchStatus.MATCHED)) {
            throw new CustomException(ErrorCode.ONLY_MATCHED_STATUS_ALLOWED);
        }

        MatchAppointment appointment = MatchAppointment.of(matchAppointmentRequest);
        matchAppointmentRepository.save(appointment);

        return MatchAppointmentResponse.of(appointment);
    }
}
