package com.gameet.notification.entity;

import com.gameet.notification.enums.MessageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchEmailSendLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long matchEmailSendLogId;

    @Column(nullable = false)
    Long matchParticipantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    MessageType messageType;

    @Column(nullable = false)
    String content;

    @Column(nullable = false)
    LocalDateTime sendAt;

    public static MatchEmailSendLog of(Long matchParticipantId, MessageType messageType, String content, LocalDateTime sendAt) {
        return MatchEmailSendLog.builder()
                .matchParticipantId(matchParticipantId)
                .messageType(messageType)
                .content(content)
                .sendAt(sendAt)
                .build();
    }
}
