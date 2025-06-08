package com.gameet.chat.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.gameet.user.dto.response.UserDetailsResponse;
import com.gameet.user.entity.User;
import com.gameet.user.entity.UserProfile;
import com.gameet.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.gameet.chat.dto.ChatMessage;
import com.gameet.chat.dto.MatchChatResponse;
import com.gameet.chat.entity.MatchChat;
import com.gameet.chat.repository.MatchChatRepository;
import com.gameet.match.entity.MatchParticipant;
import com.gameet.match.repository.MatchParticipantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchChatService {

    private final MatchChatRepository matchChatRepository;
    private final MatchParticipantRepository matchParticipantRepository;

    public MatchChatResponse saveChat(ChatMessage dto, Principal principal) {
        // match_participant_id 기반으로 엔티티 조회
        MatchParticipant participant = matchParticipantRepository.findById(dto.getMatchParticipantId())
                  .orElseThrow(() -> new IllegalArgumentException("참가자를 찾을 수 없습니다."));

        // principal 이름과 참가자 닉네임이 일치하는지 검증
        String senderNickname = participant.getUserProfile().getNickname();
        if (!senderNickname.equals(principal.getName())) {
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
                  .nickname(senderNickname)
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

    public UserDetailsResponse getOpponentInfo(Long roomId, Long myProfileId) {
        MatchParticipant opponentParticipant = matchParticipantRepository
                  .findOpponentByRoomIdAndMyProfileId(roomId, myProfileId)
                  .orElseThrow(() -> new EntityNotFoundException("상대방을 찾을 수 없습니다."));

        UserProfile opponentProfile = opponentParticipant.getUserProfile();
        User opponentUser = opponentProfile.getUser();

        return UserDetailsResponse.of(opponentUser);
    }

    public void validateUserIdMatch(Long matchParticipantId, Long userIdFromPrincipal) {
        MatchParticipant participant = matchParticipantRepository.findById(matchParticipantId)
                  .orElseThrow(() -> new IllegalArgumentException("Invalid participant ID"));

        Long participantUserId = participant.getUserProfile().getUser().getUserId();

        if (!participantUserId.equals(userIdFromPrincipal)) {
            throw new AccessDeniedException("User ID does not match participant");
        }
    }

}