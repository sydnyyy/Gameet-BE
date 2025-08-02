package com.gameet.notification.dto;

import com.gameet.match.entity.MatchAppointment;
import com.gameet.match.enums.MatchStatus;
import com.gameet.notification.enums.MessageType;

import lombok.Builder;

@Builder
public record WebSocketPayload (
        MessageType messageType,
        MatchStatus matchStatus,
        Long matchRoomId,
        String content,
        Long senderId
) {

    public static WebSocketPayload fromMatchResult(MatchStatus matchStatus, Long matchRoomId) {
        return WebSocketPayload.builder()
                .messageType(MessageType.MATCH_RESULT)
                .matchStatus(matchStatus)
                .matchRoomId(matchRoomId)
                .content(MatchStatus.getMessage(matchStatus))
                .build();
    }

    public static WebSocketPayload fromMatchAppointment(MatchAppointment matchAppointment) {
        return WebSocketPayload.builder()
                .messageType(MessageType.MATCH_APPOINTMENT)
                .matchStatus(MatchStatus.MATCHED)
                .matchRoomId(matchAppointment.getMatchRoomId())
                .content(matchAppointment.getAppointmentAt().toLocalTime() + " 예약 시간이 다가옵니다!")
                .build();
    }

    public static WebSocketPayload fromStompSubscribeEvent(String destination) {
        return WebSocketPayload.builder()
                .messageType(MessageType.STOMP_SUBSCRIBE)
                .matchStatus(null)
                .matchRoomId(null)
                .content(destination + " 구독 성공했습니다.")
                .build();

    }

    public static WebSocketPayload fromMatchChat(Long matchRoomId, Long senderId) {
        return WebSocketPayload.builder()
                .messageType(MessageType.CHAT)
                .matchStatus(null)
                .matchRoomId(matchRoomId)
                .content("새로운 채팅이 도착했습니다.")
                .senderId(senderId)
                .build();

    }

    public static WebSocketPayload fromStompSubscribe(MessageType messageType, String destination) {
        return WebSocketPayload.builder()
                .messageType(messageType)
                .content(destination + " 구독 성공")
                .build();
    }

    public static WebSocketPayload fromError(MessageType messageType) {
        return WebSocketPayload.builder()
                .messageType(messageType)
                .content(messageType.name())
                .build();
    }
}
