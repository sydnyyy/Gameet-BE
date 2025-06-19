package com.gameet.chat.service;

import com.gameet.chat.dto.ChatMessage;
import com.gameet.chat.dto.MatchParticipantsInfoResponse;
import com.gameet.chat.dto.UnreadChatResponse;
import com.gameet.chat.entity.MatchChat;
import com.gameet.notification.enums.MessageType;
import com.gameet.chat.repository.MatchChatRepository;
import com.gameet.global.exception.CustomException;
import com.gameet.global.exception.ErrorCode;
import com.gameet.match.entity.MatchRoom;
import com.gameet.match.enums.MatchStatus;
import com.gameet.match.repository.MatchRoomRepository;
import com.gameet.notification.dto.NotificationPayload;
import com.gameet.notification.service.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.gameet.match.entity.MatchParticipant;
import com.gameet.match.repository.MatchParticipantRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchChatService {

    private final MatchParticipantRepository matchParticipantRepository;
    private final MatchRoomRepository matchRoomRepository;
    private final MatchChatRepository matchChatRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    @Transactional
    public MatchChat saveChat(ChatMessage message) {
        MatchParticipant sender = matchParticipantRepository
                  .findById(message.getMatchParticipantId())
                  .orElseThrow(() -> new EntityNotFoundException("참가자를 찾을 수 없습니다."));

        MatchChat chat = MatchChat.builder()
                  .matchParticipant(sender)
                  .messageType(message.getMessageType())
                  .content(message.getContent())
                  .sendAt(message.getSendAt())
                  .build();

        MatchChat savedChat = matchChatRepository.save(chat);

        // 알림 보낼 대상 식별
        MatchRoom room = sender.getMatchRoom();
        List<MatchParticipant> participants = room.getMatchParticipants();

        for (MatchParticipant participant : participants) {
            Long receiverId = participant.getUserProfile().getUserProfileId();

            // 본인은 제외
            if (!participant.getMatchParticipantId().equals(sender.getMatchParticipantId())) {
                notificationService.sendChatNotification(receiverId, room.getMatchRoomId(), sender.getMatchParticipantId());
            }
        }

        return savedChat;
    }

    public List<ChatMessage> getChatMessagesByRoomId(Long matchRoomId) {
        List<MatchChat> chats = matchChatRepository.findByMatchRoomId(matchRoomId);

        return chats.stream().map(chat -> {
            MatchParticipant participant = chat.getMatchParticipant();
            return ChatMessage.builder()
                      .messageType(chat.getMessageType())
                      .content(chat.getContent())
                      .matchParticipantId(participant.getMatchParticipantId())
                      .sendAt(chat.getSendAt())
                      .build();
        }).toList();
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

        // 두 참가자 모두에게 알림 전송
        List<MatchParticipant> participants = matchRoom.getMatchParticipants();
        for (MatchParticipant participant : participants) {
            String userId = participant.getUserProfile().getUserProfileId().toString();

            NotificationPayload payload = NotificationPayload.builder()
                      .messageType(MessageType.MATCH_RESULT)
                      .matchStatus(MatchStatus.COMPLETED)
                      .matchRoomId(matchRoomId)
                      .content("매칭이 종료되었습니다.")
                      .build();
            messagingTemplate.convertAndSendToUser(userId, "/queue/notify", payload);
        }
    }

    @Transactional
    public MatchParticipantsInfoResponse getMatchParticipantsInfo(Long matchRoomId, Long userId) {
        List<MatchParticipant> matchParticipants = matchParticipantRepository.findByMatchRoom_matchRoomId(matchRoomId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MATCH_PARTICIPANT));

        return MatchParticipantsInfoResponse.of(matchParticipants, userId);
    }

    @Transactional
    public void updateLastReadAt(Long matchParticipantId) {
        MatchParticipant participant = matchParticipantRepository.findById(matchParticipantId)
                  .orElseThrow(() -> new EntityNotFoundException("참가자를 찾을 수 없습니다."));

        participant.setLastReadAtNow();
    }

    public long getUnreadCount(Long userProfileId) {
        MatchParticipant participant = matchParticipantRepository
                  .findMatchedParticipantByUserProfileId(userProfileId)
                  .orElseThrow(() -> new EntityNotFoundException("매칭된 참가자를 찾을 수 없습니다."));

        Long participantId = participant.getMatchParticipantId();
        Long roomId = participant.getMatchRoom().getMatchRoomId();
        LocalDateTime lastReadAt = Optional.ofNullable(participant.getLastReadAt())
                  .orElse(LocalDateTime.of(1000, 1, 1, 0, 0));

        return matchChatRepository.countUnreadMessages(roomId, participantId, lastReadAt);
    }

}