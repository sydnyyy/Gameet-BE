package com.gameet.match.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.match.annotation.MatchUserLockable;
import com.gameet.match.domain.MatchCondition;
import com.gameet.match.dto.request.MatchConditionRequest;
import com.gameet.match.entity.MatchParticipant;
import com.gameet.match.entity.MatchRoom;
import com.gameet.match.entity.MatchSuccessCondition;
import com.gameet.match.entity.MatchSuccessGamePlatform;
import com.gameet.match.entity.MatchSuccessGamePlatformId;
import com.gameet.match.entity.MatchSuccessPreferredGenre;
import com.gameet.match.entity.MatchSuccessPreferredGenreId;
import com.gameet.match.enums.MatchStatus;
import com.gameet.match.mapper.MatchConditionMapper;
import com.gameet.match.repository.MatchParticipantRepository;
import com.gameet.match.repository.MatchRepository;
import com.gameet.match.repository.MatchRoomRepository;
import com.gameet.user.entity.UserProfile;
import com.gameet.user.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchService {

    private final MatchRepository matchRepository;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final MatchRoomRepository matchRoomRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final UserProfileRepository userProfileRepository;

    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public void tryMatch(Long userId, MatchConditionRequest matchConditionRequest) {
        MatchStatus matchStatus = getMatchStatus(userId);
        if (matchStatus == MatchStatus.SEARCHING) {
            throw new CustomException(ErrorCode.ALREADY_SEARCHING);
        } else if (matchStatus == MatchStatus.MATCHED) {
            throw new CustomException(ErrorCode.ALREADY_MATCHED);
        }

        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        MatchCondition matchCondition = MatchConditionMapper.from(userProfile, matchConditionRequest);

        Set<Long> allMatchUsers = matchRepository.getAllMatchUsers();

        for (Long otherMatchUserId : allMatchUsers) {
            if (matchRepository.tryLock(otherMatchUserId)) {
                try {
                    MatchCondition otherMatchCondition = matchRepository.getMatchConditionByUserId(otherMatchUserId);

                    boolean isMatchCompatible = MatchConditionMatcher.isMatchCompatible(otherMatchCondition, matchCondition);
                    if (isMatchCompatible) {
                        removeMatch(otherMatchUserId);

                        cancelScheduledTask(otherMatchUserId);

                        Map<Long, MatchCondition> userMatchCondition = new HashMap<>();
                        userMatchCondition.put(userId, matchCondition);
                        userMatchCondition.put(otherMatchUserId, otherMatchCondition);

                        Long matchRoomId = createMatchRoom(List.of(userMatchCondition));

                        // TODO 알림 보내기


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
    }

    private void retryMatchWithRelaxedCondition(Long userId, MatchCondition matchCondition) {
        List<Runnable> RelaxedConditionSteps = List.of(
                () -> matchCondition.setGameSkillLevel(null),
                () -> matchCondition.setPlayStyle(null),
                () -> matchCondition.setIsVoice(null)
        );

        retryMatch(userId, matchCondition, RelaxedConditionSteps, 0);
    }

    @MatchUserLockable
    private void retryMatch(Long userId, MatchCondition matchCondition, List<Runnable> steps, int stepIndex) {
        if (stepIndex >= steps.size()) {
            ScheduledFuture<?> future = scheduledExecutorService.schedule(() -> failMatch(userId), 15, TimeUnit.SECONDS);
            scheduledTasks.put(userId, future);
            return;
        }

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

                        removeMatch(userId);
                        removeMatch(otherMatchUserId);

                        cancelScheduledTask(otherMatchUserId);

                        Map<Long, MatchCondition> userMatchCondition = new HashMap<>();
                        userMatchCondition.put(userId, matchCondition);
                        userMatchCondition.put(otherMatchUserId, otherMatchCondition);

                        Long matchRoomId = createMatchRoom(List.of(userMatchCondition));

                        // TODO 알림 보내기

                        return;
                    }
                } finally {
                    matchRepository.releaseLock(otherMatchUserId);
                }
            }
        }

        matchRepository.saveMatchCondition(userId, convertMatchConditionToStringMap(matchCondition));

        ScheduledFuture<?> future = scheduledExecutorService.schedule(() ->
                retryMatch(userId, matchCondition, steps, stepIndex + 1), 5, TimeUnit.SECONDS
        );
        scheduledTasks.put(userId, future);
    }

    @MatchUserLockable
    private void failMatch(Long userId) {
        removeMatch(userId);

        // TODO 알림 보내기
    }

    @MatchUserLockable
    public void cancelMatch(Long userId) {
        removeMatch(userId);
        cancelScheduledTask(userId);
    }

    public MatchStatus getMatchStatus(Long userId) {
        if (matchRepository.isMatchUserExists(userId)) {
            return MatchStatus.SEARCHING;
        }

        boolean isMatched = matchParticipantRepository.existsByMatchParticipantIdAndMatchRoom_matchStatus(userId, MatchStatus.MATCHED);
        if (isMatched) {
            return MatchStatus.MATCHED;
        }

        return MatchStatus.NONE;
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

    private void removeMatch(Long userId) {
        matchRepository.removeMatchUser(userId);
        matchRepository.removeMatchCondition(userId);
    }

    private Long createMatchRoom(List<Map<Long, MatchCondition>> userMatchConditions) {
        MatchRoom matchRoom = MatchRoom.builder()
                .matchStatus(MatchStatus.MATCHED)
                .build();

        userMatchConditions.forEach(map -> {
            map.forEach((userId, matchCondition) -> {
                UserProfile userProfile = userProfileRepository.getReferenceById(userId);

                MatchSuccessCondition matchSuccessCondition = MatchSuccessCondition.builder()
                        .gameSkillLevel(matchCondition.getGameSkillLevel())
                        .isAdultMatchAllowed(matchCondition.getIsAdultMatchAllowed())
                        .isVoice(matchCondition.getIsVoice())
                        .minMannerScore(matchCondition.getMinMannerScore())
                        .playStyle(matchCondition.getPlayStyle())
                        .build();

                matchCondition.getGamePlatforms().forEach(gamePlatform -> {
                    MatchSuccessGamePlatformId id = MatchSuccessGamePlatformId.builder()
                            .gamePlatform(gamePlatform)
                            .build();

                    matchSuccessCondition.addMatchSuccessGamePlatform(
                            MatchSuccessGamePlatform.builder()
                                    .id(id)
                                    .build()
                    );
                });

                matchCondition.getPreferredGenres().forEach(preferredGenre -> {
                    MatchSuccessPreferredGenreId id = MatchSuccessPreferredGenreId.builder()
                            .preferredGenre(preferredGenre)
                            .build();

                    matchSuccessCondition.addMatchSuccessPreferredGenre(
                            MatchSuccessPreferredGenre.builder()
                                    .id(id)
                                    .build()
                    );
                });

                MatchParticipant matchParticipant = MatchParticipant.builder()
                        .userProfile(userProfile)
                        .build();

                matchParticipant.setMatchSuccessCondition(matchSuccessCondition);

                matchRoom.addParticipant(matchParticipant);
            });
        });

        MatchRoom savedMatchRoom = matchRoomRepository.save(matchRoom);
        return savedMatchRoom.getMatchRoomId();
    }

    private void cancelScheduledTask(Long userId) {
        ScheduledFuture<?> future = scheduledTasks.remove(userId);
        if (future != null) {
            future.cancel(false);
        }
    }
}
