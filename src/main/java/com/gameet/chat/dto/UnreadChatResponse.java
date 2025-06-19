package com.gameet.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UnreadChatResponse {
    private Long unreadCount;
    private Long matchParticipantId;
    private LocalDateTime lastReadAt;
}
