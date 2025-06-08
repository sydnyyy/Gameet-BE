package com.gameet.chat.dto;

import java.time.LocalDateTime;

import com.gameet.chat.enums.MessageType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MatchChatResponse {
    private Long matchChatId;
    private Long matchRoomId;
    private String nickname;
    private MessageType messageType;
    private String content;
    private LocalDateTime sendAt;
}
