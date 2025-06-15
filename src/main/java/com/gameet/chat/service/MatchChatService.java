package com.gameet.chat.service;

import com.gameet.chat.dto.ChatMessage;
import com.gameet.chat.entity.MatchChat;
import com.gameet.chat.repository.MatchChatRepository;
import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.match.entity.MatchRoom;
import com.gameet.match.enums.MatchStatus;
import com.gameet.match.repository.MatchRoomRepository;
import org.springframework.stereotype.Service;

import com.gameet.match.entity.MatchParticipant;
import com.gameet.match.repository.MatchParticipantRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchChatService {

    private final MatchParticipantRepository matchParticipantRepository;
    private final MatchRoomRepository matchRoomRepository;
    private final MatchChatRepository matchChatRepository;

    @Transactional
    public MatchChat saveChat(ChatMessage message) {
        MatchParticipant participant = matchParticipantRepository
                  .findById(message.getMatchParticipantId())
                  .orElseThrow(() -> new EntityNotFoundException("참가자를 찾을 수 없습니다."));

        MatchChat chat = MatchChat.builder()
                  .matchParticipant(participant)
                  .messageType(message.getMessageType())
                  .content(message.getContent())
                  .sendAt(message.getSendAt())
                  .build();

        return matchChatRepository.save(chat);
    }


    public Long getMyParticipantId(Long roomId, Long userId) {
        MatchParticipant participant = matchParticipantRepository
                  .findByMatchRoomIdAndUserId(roomId, userId)
                  .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PARTICIPANT));

        return participant.getMatchParticipantId();
    }

    public Long getMatchRoomIdByParticipantId(Long matchParticipantId) {
        MatchParticipant participant = matchParticipantRepository.findById(matchParticipantId)
                  .orElseThrow(() -> new EntityNotFoundException("참가자를 찾을 수 없습니다."));

        return participant.getMatchRoom().getMatchRoomId();
    }

    @Transactional
    public void completeMatch(Long matchRoomId) {
        MatchRoom matchRoom = matchRoomRepository.findById(matchRoomId)
                  .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MATCH_ROOM));
        matchRoom.setMatchStatus(MatchStatus.COMPLETED);
    }
}