package com.gameet.chat.service;

import java.util.stream.Collectors;

import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.match.entity.MatchRoom;
import com.gameet.match.enums.MatchStatus;
import com.gameet.match.repository.MatchRoomRepository;
import org.springframework.stereotype.Service;

import com.gameet.chat.dto.OpponentProfileResponse;
import com.gameet.match.entity.MatchParticipant;
import com.gameet.match.repository.MatchParticipantRepository;
import com.gameet.user.entity.UserGamePlatform;
import com.gameet.user.entity.UserPreferredGenre;
import com.gameet.user.entity.UserProfile;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchChatService {

    private final MatchParticipantRepository matchParticipantRepository;
    private final MatchRoomRepository matchRoomRepository;

    public Long getMyParticipantId(Long roomId, Long userId) {
        MatchParticipant participant = matchParticipantRepository
                  .findByMatchRoomIdAndUserId(roomId, userId)
                  .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PARTICIPANT));

        return participant.getMatchParticipantId();
    }

    public OpponentProfileResponse getOpponentProfile(Long roomId, Long myProfileId) {
        MatchParticipant opponent = matchParticipantRepository
                  .findOpponentProfileByRoomIdAndExcludeMyProfile(roomId, myProfileId)
                  .orElseThrow(() -> new EntityNotFoundException("상대방을 찾을 수 없습니다."));

        UserProfile profile = opponent.getUserProfile();

        return OpponentProfileResponse.builder()
                  .userProfileId(profile.getUserProfileId())
                  .nickname(profile.getNickname())
                  .age(profile.getAge())
                  .showAge(profile.getShowAge())
                  .gender(profile.getGender())
                  .preferredGenres(
                            profile.getPreferredGenres().stream()
                                      .map(UserPreferredGenre::getPreferredGenre)
                                      .collect(Collectors.toList())
                  )
                  .gamePlatforms(
                            profile.getGamePlatforms().stream()
                                      .map(UserGamePlatform::getGamePlatform)
                                      .collect(Collectors.toList())
                  )
                  .playStyle(profile.getPlayStyle())
                  .gameSkillLevel(profile.getGameSkillLevel())
                  .isAdultMatchAllowed(profile.getIsAdultMatchAllowed())
                  .isVoice(profile.getIsVoice())
                  .build();
    }

    @Transactional
    public void completeMatch(Long matchRoomId) {
        MatchRoom matchRoom = matchRoomRepository.findById(matchRoomId)
                  .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MATCH_ROOM));
        matchRoom.setMatchStatus(MatchStatus.COMPLETED);
    }
}