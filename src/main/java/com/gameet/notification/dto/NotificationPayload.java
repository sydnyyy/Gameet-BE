package com.gameet.notification.dto;

import com.gameet.match.entity.MatchAppointment;
import com.gameet.match.enums.MatchStatus;
import com.gameet.notification.enums.MessageType;

import lombok.Builder;

@Builder
public record NotificationPayload (
        MessageType messageType,
        MatchStatus matchStatus,
        Long matchRoomId,
        String content,
        Long senderId
) {

    public static NotificationPayload fromMatchResult(MatchStatus matchStatus, Long matchRoomId) {
        return NotificationPayload.builder()
                .messageType(MessageType.MATCH_RESULT)
                .matchStatus(matchStatus)
                .matchRoomId(matchRoomId)
                .content(MatchStatus.getMessage(matchStatus))
                .build();
    }

    public static NotificationPayload fromMatchAppointment(MatchAppointment matchAppointment) {
        return NotificationPayload.builder()
                .messageType(MessageType.MATCH_APPOINTMENT)
                .matchStatus(MatchStatus.MATCHED)
                .matchRoomId(matchAppointment.getMatchRoomId())
                .content(matchAppointment.getAppointmentAt().toLocalTime() + " 예약 시간이 다가옵니다!")
                .build();
    }

    public static NotificationPayload fromStompSubscribeEvent(String destination) {
        return NotificationPayload.builder()
                .messageType(MessageType.STOMP_SUBSCRIBE)
                .matchStatus(null)
                .matchRoomId(null)
                .content(destination + " 구독 성공했습니다.")
                .build();

    }

    public static NotificationPayload fromMatchChat(Long matchRoomId, Long senderId) {
        return NotificationPayload.builder()
                .messageType(MessageType.CHAT)
                .matchStatus(null)
                .matchRoomId(matchRoomId)
                .content("새로운 채팅이 도착했습니다.")
                .senderId(senderId)
                .build();

    }
}
