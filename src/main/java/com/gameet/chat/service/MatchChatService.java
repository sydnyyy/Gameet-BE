package com.gameet.chat.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.gameet.chat.dto.ChatMessage;
import com.gameet.chat.dto.MatchChatResponse;
import com.gameet.chat.dto.OpponentProfileResponse;
import com.gameet.chat.entity.MatchChat;
import com.gameet.chat.repository.MatchChatRepository;
import com.gameet.match.entity.MatchParticipant;
import com.gameet.match.repository.MatchParticipantRepository;
import com.gameet.user.entity.UserGamePlatform;
import com.gameet.user.entity.UserPreferredGenre;
import com.gameet.user.entity.UserProfile;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchChatService {

    private final MatchChatRepository matchChatRepository;
    private final MatchParticipantRepository matchParticipantRepository;

    public MatchChatResponse saveChat(ChatMessage dto, Principal principal) {
        Long userId = Long.parseLong(principal.getName()); // User ID로 비교
        MatchParticipant participant = matchParticipantRepository.findById(dto.getMatchParticipantId())
                  .orElseThrow(() -> new IllegalArgumentException("참가자를 찾을 수 없습니다."));

        if (!participant.getUserProfile().getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("본인의 메시지만 보낼 수 있습니다.");
        }

        // 채팅 엔티티 생성 및 저장
        MatchChat chat = MatchChat.builder()
                  .matchParticipant(participant)
                  .messageType(dto.getMessageType())
                  .content(dto.getContent())
                  .sendAt(LocalDateTime.now())
                  .build();

        MatchChat savedChat = matchChatRepository.save(chat);

        // 응답 DTO 구성
        return MatchChatResponse.builder()
                  .matchChatId(savedChat.getMatchChatId())
                  .matchRoomId(participant.getMatchRoom().getMatchRoomId())
                  .messageType(savedChat.getMessageType())
                  .content(savedChat.getContent())
                  .sendAt(savedChat.getSendAt())
                  .build();
    }

    public List<MatchChatResponse> getChats(Long matchRoomId) {
        List<MatchChat> chats = matchChatRepository.findByMatchRoomId(matchRoomId);

        return chats.stream()
                  .map(chat -> MatchChatResponse.builder()
                            .matchChatId(chat.getMatchChatId())
                            .matchRoomId(chat.getMatchParticipant().getMatchRoom().getMatchRoomId())
                            .nickname(chat.getMatchParticipant().getUserProfile().getNickname())
                            .messageType(chat.getMessageType())
                            .content(chat.getContent())
                            .sendAt(chat.getSendAt())
                            .build())
                  .collect(Collectors.toList());
    }

    public void validateUserIdMatch(Long matchParticipantId, Long userIdFromPrincipal) {
        MatchParticipant participant = matchParticipantRepository.findById(matchParticipantId)
                  .orElseThrow(() -> new IllegalArgumentException("Invalid participant ID"));

        Long participantUserId = participant.getUserProfile().getUser().getUserId();

        if (!participantUserId.equals(userIdFromPrincipal)) {
            throw new AccessDeniedException("User ID does not match participant");
        }
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
}